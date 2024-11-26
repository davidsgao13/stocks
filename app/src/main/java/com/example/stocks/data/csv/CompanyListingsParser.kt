package com.example.stocks.data.csv

import android.util.Log
import com.example.stocks.domain.model.CompanyListing
import com.opencsv.CSVReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The actual implementation of the CSVParser interface. We declare it as a Singleton so that the
 * same instance of it can be used throughout the app, and we include @Inject. This means without
 * constructor variables, so Dagger/Hilt knows how to provide it to other classes for
 * dependency injection and we don't need to write a @Provides function for it in the AppModule.
 * If the class had arguments, and all the constructor arguments were injected as well, then we
 * also wouldn't need to add a @Provides function for it.
 */
@Singleton
class CompanyListingsParser @Inject constructor() : CSVParser<CompanyListing> {

    /**
     * We create csvReader from an InputStreamReader(), which requires an InputStream, which
     * we've added as a constructor argument. Then, csvReader will do the following:
     * - readAll() to read everything row
     * - drop(1) to remove the first field (row), which is just a description.
     * - mapNotNull { }, which calls the map { } function on any record that is not null.
     *   We do this so we can read each individual line and assign our values from that record.
     *
     *   From there, we retrieve the values of each row:
     *   - symbol is the first value, so we retrieve it with line.getOrNull(0)
     *   - name is the second value; line.getOrNull(1)
     *   - exchange is the third value; line.getOrNull(2)
     *
     *   Then from those values, we create a CompanyListing object. Note that in each of the
     *   assignments, we set property = property ?: return@mapNotNull null. This is because each
     *   of these properties could be null. In the event that they are, we want to return
     *   @mapNotNull null, which will return null to the outer mapNotNull function, thereby
     *   short circuiting the entry and moving on to the next potential entry.
     *
     *   Lastly, we need to call .close() on our csvReader to ensure there are no memory leaks.
     */
    override suspend fun parse(stream: InputStream): List<CompanyListing> {
        val csvReader = CSVReader(InputStreamReader(stream))
        // We need to switch the execution of the code block to the IO thread pool, which is
        // optimized for tasks that involve blocking operations, like file I/O, database queries,
        // and network requests. We do this because we're reading from an InputStream, which is
        // an I/O bound task. By running parse on the I/O thread, we aren't going to be blocking
        // the main thread.
        return withContext(Dispatchers.IO) {
            csvReader
                .readAll()
                .drop(1)
                // mapNotNull is a Kotlin collection transformation function. It operates on an
                // iterable (like List), applying given transformation to each element, and
                // filtering out any null results. That means it doesn't parse any element that
                // is null. If the whole object is null, then the entire line is skipped.
                .mapNotNull { line ->
                    val symbol = line.getOrNull(0)
                    val name = line.getOrNull(1)
                    val exchange = line.getOrNull(2)
                    CompanyListing(
                        // return@mapNotNull explicitly returns null for the current iteration
                        // of the mapNotNull transformation.not the outer parse function.
                        // Since we don't want to create any record that has any of these values
                        // as null, returning null to the mapNotNull { } will short-circuit the
                        // current iteration and skip to the next line, since mapNotNull will skip
                        // the line if the whole object is null, which we are returning with
                        // return@mapNotNull null.
                        name = name ?: return@mapNotNull null,
                        symbol = symbol ?: return@mapNotNull null,
                        exchange = exchange ?: return@mapNotNull null
                    )
                }
                // .also is a scope function, and is called after the preceding operations have been
                // applied, but not before a return function is called. It provides the purpose
                // of offering a side effect, like cleaning up resources or logging, without
                // impacting the main flow of the operation. In this case, immediately after the
                // transformation, .also fires and cleans up our csvReader resource.
                .also {
                    csvReader.close()
                }
        }
    }
}