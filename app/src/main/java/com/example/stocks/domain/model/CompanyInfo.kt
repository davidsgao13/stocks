package com.example.stocks.domain.model

import com.squareup.moshi.Json

/**
 * Domain level object that will be used by our application.
 */

data class CompanyInfo(
    val symbol: String,
    val description: String,
    val name: String,
    val country: String,
    val industry: String
)