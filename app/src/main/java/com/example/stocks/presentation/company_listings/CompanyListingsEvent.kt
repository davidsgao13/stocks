package com.example.stocks.presentation.company_listings

/**
 * We want to create an object for all the Events that a user can take on a screen. These Events
 * represent user actions, everything that a user could do that would lead to something happening.
 * For example, refreshing. Swiping down would refresh the company listing list. We want to send
 * that Event to the ViewModel so that the ViewModel can then handle the business logic and
 * actually implement the result of that Event.
 */
sealed class CompanyListingsEvent {
    object Refresh: CompanyListingsEvent()
    data class OnSearchQueryChanged(val query: String) : CompanyListingsEvent()
}