package com.example.stocks.presentation.company_info

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stocks.domain.model.IntradayInfo
import kotlin.math.round
import kotlin.math.roundToInt

/**
 * Creates a StockChart composable using Canvas
 */

@Composable
fun StockChart(
    infos: List<IntradayInfo> = emptyList(),
    modifier: Modifier = Modifier,
    graphColor: Color = Color.Green
) {
    // Signifies the amount of spacing between each of the axes of the chart to the edge of the view
    // Unlike the below values, assigning spacing to a literal constant doesn't cost anything
    // computationally, so there's no reason to wrap it with a remember { } block
    val spacing = 100f
    val numberYAxisLabels = 5f
    val yAxisSpacing = 5

    /**
     * In Jetpack Compose, recomposition occurs whenever the State of the UI changes. Without the
     * remember block, the transparency computation would run every single time the StockChart
     * recomposes. By wrapping it in a remember block, Compose will only ever compute the
     * transparency once and reuse the stored value so long as the inputs (dependencies) are the
     * same across subsequent recompositions. We use remember in this instance since the
     * transparency never changes, so there's no point in trying to recompose it.
     */
    val transparentGraphColor = remember {
        graphColor.copy(0.5f)
    }
    // Similarly, we don't need to recalculate the upperValue after the first composition
    // We pass in infos as the input. This means if infos ever changes, then we will recompose
    // and recalculate upperValue and lowerValue, since the inputs (dependencies) are no longer
    // the same across subsequent recompositions.
    val upperValue = remember(infos) {
        // Using integer rounding to make the graph cleaner. This is the highest value among all
        // the values in infos
        infos.maxOfOrNull { it.close }?.plus(1)?.roundToInt() ?: 0
    }
    val lowerValue = remember(infos) {
        // Floor rounding to make the graph cleaner. This is the lowest value among all the values
        // in infos
        infos.minOfOrNull { it.close }?.toInt() ?: 0
    }
    val density = LocalDensity.current
    // Using Android's native canvas to draw the text. Whenever density changes, we recalculate
    // what the textPaint object is
    val textPaint = remember(density) {
        Paint().apply {
            color = android.graphics.Color.WHITE
            textAlign = Paint.Align.CENTER
            // Converts the 12sp to pixels for canvas drawing
            textSize = density.run { 12.sp.toPx() }
        }
    }
    Canvas(modifier = modifier) {
        // Calculate space between each of the hour marks by taking the width of the Canvas
        // (size.width) subtracting the spacing, then dividing by the number of infos we have,
        // infos being the X-axis, as it's a list of the intraday info for a specific stock
        val spacePerHour = (size.width - spacing) / infos.size
        // Rather than take every hour, we only show an x-axis label every two hours. This is to
        // avoid cluttering
        (infos.indices step 2).forEach { i ->
            val info = infos[i]
            val hour = info.date.hour
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    hour.toString(),
                    spacing + i * spacePerHour,
                    size.height - yAxisSpacing,
                    textPaint
                )
            }
        }
        val priceStep = upperValue - lowerValue / numberYAxisLabels
        (0 until 5).forEach { i ->
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    round(lowerValue + priceStep * i).toString(),
                    30f,
                    size.height - spacing - i * size.height / numberYAxisLabels,
                    textPaint
                )
            }
        }
        var lastX = 0f
        val strokePath = Path().apply {
            // We can call functions like moveTo and lineTo in order to make paths on our Path()
            // object in order to more easily draw a non-linear Path
            val height = size.height
            (infos.indices).forEach { i ->
                val info = infos[i]
                // Get the position for the next point; if the next point is null, then we consider
                // the nextInfo to just be infos.last, which will just draw a line from the last
                // value (which has no nextInfo) to the last value, which will result in being the
                // line overlapped so it doesn't appear
                val nextInfo = infos.getOrNull(i + 1) ?: infos.last()

                /**
                 * Calculates the relative position of info (infos[i]) on the graph. This is the
                 * percentage value of where on the graph the point should be drawn. It's just
                 * the difference between the current value and the minimum value, divided
                 * by the difference between the highest value and the lowest value. That gives us
                 * the percentage of where the point will be.
                 *
                 * highest = 100
                 * lowest = 50
                 * currentPoint = 75
                 *
                 * (75 - 50) / (100 - 50) = 25 / 50 = .5; this means the currentPoint (75) is
                 * in the .5 y position of the graph between 50 to 100. That means it's halfway
                 * between 50 and 100, which is 100% correct. It represents the normalized ratio
                 * of the position of the current point (info.close) between the top (upperValue)
                 * and bottom (lowerValue) of the graph.
                 */

                val leftRatio = (info.close - lowerValue) / (upperValue - lowerValue)
                // Calculates the relative position of nextInfo (infos.getOrNull(i + 1) on the graph
                val rightRatio = (nextInfo.close - lowerValue) / (upperValue - lowerValue)

                val calculatePosition: (Int, Double) -> Pair<Float, Float> = { index, ratio ->
                    val x = spacing + index * spacePerHour
                    val y = height - spacing - (ratio * height).toFloat()
                    // to keyword generates a Pair. x to y = Pair(x, y)
                    x to y
                }
                val (x1, y1) = calculatePosition(i, leftRatio)
                val (x2, y2) = calculatePosition(i + 1, rightRatio)

                if (i == 0) {
                    // Coordinate of the left point
                    moveTo(
                        x1,
                        y1
                    )
                    // Makes a smooth line; x1 + x2 / 2f is math related to make a curved line
                }
                lastX = x1 + x2 / 2f
                quadraticTo(x1, y1, lastX / 2f, (y1 + y2) / 2f)
            }
        }
        val fillPath = android.graphics.Path(strokePath.asAndroidPath())
            .asComposePath()
            .apply {
                lineTo(lastX, size.height - spacing)
                lineTo(spacing, size.height - spacing)
                close()
            }
        drawPath(
            path = fillPath,
            // Creates a gradient for the fill path
            brush = Brush.verticalGradient(
                // Transparent from top to bottom of the first color in the list to the second color
                // in the list
                colors = listOf(
                    transparentGraphColor,
                    Color.Transparent
                ),
                endY = size.height - spacing
            )
        )
        drawPath(strokePath,
            graphColor,
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        )
    }
}