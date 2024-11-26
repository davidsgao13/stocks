package com.example.stocks.presentation.company_listings

import com.example.stocks.domain.model.CompanyListing

/**
 * The State for our CompanyListingsViewModel; it represents everything that belongs to the
 * ViewModel on our CompanyListingsScreen.
 *
 * @listings are all the CompanyListings that we will show in our LazyColumn
 * @isLoading determines whether or not the page is currently loading and should show loading UI
 * @isRefreshing determines if the page is currently in the process of refreshing, so we can show
 * a spinner and so that we don't make any dangerous changes to the UI while it's refreshing
 * @searchQuery is the text that will be retrieved from the search bar
 */

data class CompanyListingsState(
    val listings: List<CompanyListing> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val searchQuery: String = ""
)
