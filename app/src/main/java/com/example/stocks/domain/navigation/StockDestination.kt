package com.example.stocks.domain.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface StockDestination : NavigationDestination {
    @Serializable
    object CompanyListings : StockDestination {
        override val route = "company_listings"
    }

    @Serializable
    data class CompanyDetails(val symbol: String) : StockDestination {
        override val route = "company_details"
    }
}