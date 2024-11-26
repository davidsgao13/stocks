package com.example.stocks.data.csv

import com.example.stocks.data.mapper.toIntradayInfo
import com.example.stocks.data.remote.dto.IntradayInfoDto
import com.example.stocks.domain.model.IntradayInfo
import com.opencsv.CSVReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import java.time.LocalDateTime
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
class IntradayInfoParser @Inject constructor() : CSVParser<IntradayInfo> {
    override suspend fun parse(stream: InputStream): List<IntradayInfo> {
        val csvReader = CSVReader(InputStreamReader(stream))
        /**
         * We are interested in columns 0 and 4, because they give us the time for the data point
         * and the close value for the data point. It may be helpful to use column 1 (open)
         * or column 2 (high) or column 3 (low) at some point, so keep those in mind.
         */
        return withContext(Dispatchers.IO) {
            csvReader.readAll()
                .drop(1)
                .mapNotNull { line ->
                    // Similar to the CompanyListingsParser, we should return null to short circuit
                    // the loop in the event that one of these values is null, so that we don't
                    // process a null value under any circumstances. This also ensures that we
                    // don't need to use null safety when we create the IntradyInfoDto later
                    val timestamp = line.getOrNull((0)) ?: return@mapNotNull null
                    val close = line.getOrNull(4) ?: return@mapNotNull null
                    // We convert the data to a DTO object so that we may return it later
                    // as a domain object after conversion.
                    val dto = IntradayInfoDto(
                        timestamp,
                        close.toDouble()
                    )
                    dto.toIntradayInfo()
                }.filter {
                    // Depending on the timezone, some entries in our table have a different
                    // date than others (e.g. you could have entries with 11-26 and 11-25 on the
                    // same table). As a result, we filter the retrieved values so that we're
                    // only showing yesterday's values, and not entries from any days prior.
                    // We get the current date, subtract one day, find the day of the month, and if
                    // that day of the month is equal to the date in the entry, we'll keep the entry
                    it.date.dayOfMonth == LocalDateTime.now().minusDays(1).dayOfMonth
                }.sortedBy {
                    // Sort by the hour of each entry
                    it.date.hour
                }
                .also {
                    csvReader.close()
                }
        }
    }
}