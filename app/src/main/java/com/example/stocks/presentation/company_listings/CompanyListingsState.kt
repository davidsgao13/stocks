package com.example.stocks.presentation.company_listings

import com.example.stocks.domain.model.CompanyListing

/**
 * The State for our CompanyListingsViewModel; it represents everything that belongs to the
 * ViewModel on our UI screen.
 */

data class CompanyListingsState(
    val listings: List<CompanyListing> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val searchQuery: String = ""
)
