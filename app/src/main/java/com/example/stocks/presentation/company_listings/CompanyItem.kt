package com.example.stocks.presentation.company_listings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stocks.domain.model.CompanyListing

@Composable
fun CompanyItem(
    company: CompanyListing,
    modifier: Modifier = Modifier
) {
    /**
     * Why are we accepting a modifier as an argument for CompanyItem? Because this modifier that
     * we're passing in will be used on the root container (Row) of the composable. The child
     * composables inside of the Row have their own modifiers and don't inherit from the parent,
     * but in the case of the Row, we want to inherit whatever size we're being passed so that
     * we can make the CompanyItem reusable across different sizes. In this case, the LazyColumn
     * is instantiating CompanyItem views, so it passes in a modifier of fillMaxWidth() to the
     * CompanyItem, with maxWidth() in relation to the parent LazyColumn. This ensures that on
     * the CompanyListingsScreen, each CompanyItem extends the max width of its parent composable.
     */

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = company.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = company.exchange,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text(
                text = "(${company.symbol})",
                fontWeight = FontWeight.Light,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}