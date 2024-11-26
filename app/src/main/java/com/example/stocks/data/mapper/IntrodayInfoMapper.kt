package com.example.stocks.data.mapper

import com.example.stocks.data.remote.dto.IntradayInfoDto
import com.example.stocks.domain.model.IntradayInfo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Mapping from the IntradayInfo DTO to the domain model.
 *
 */

fun IntradayInfoDto.toIntradayInfo() : IntradayInfo {
    // Our IntradayInfoDto has a String date value; we want to convert that to a LocalDateTime
    // object in IntradayInfo. First we need to set the pattern for the date value.
    val pattern = "yyyy-MM-dd HH:mm:ss"
    // Next we create a formatter to parse the pattern for use to format the date Strings. We
    // set the current device's locale so that we can properly convert it into the correct format
    // (e.g. Europe may have a dd-MM-YYYY format)
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
    // Finally we create a LocalDateTime object by calling the static parse() function, providing
    // the Date string from IntradayInfoDto and our formatter, which should convert that String
    // (timestamp) using the formatter into a LocalDateTime object of the same pattern as our
    // pattern.
    val localDateTime = LocalDateTime.parse(timestamp, formatter)
    return IntradayInfo(
        date = localDateTime,
        close = close
    )
}