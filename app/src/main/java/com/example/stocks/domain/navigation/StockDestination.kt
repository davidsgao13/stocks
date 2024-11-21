package com.example.stocks.domain.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface StockDestination : NavigationDestination {
    @Serializable
    object CompanyListings : StockDestination {
        override val route = "company_listings"
    }

    @Serializable
    object MainScreen : StockDestination {
        override val route = "main_screen"
    }

    @Serializable
    data class CompanyDetails(val symbol: String) : StockDestination {
        override val route = "company_details"
    }
}