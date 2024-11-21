package com.example.stocks.presentation.company_listings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stocks.domain.navigation.StockDestination
import com.example.stocks.domain.repository.StockRepository
import com.example.stocks.domain.use_case.navigation.NavigationUseCase
import com.example.stocks.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * The purpose of the ViewModel is to access the data layer functions using abstractions, taking
 * the data retrieved from those repository abstractions, and mapping it to States that we can
 * show in our UI. The ViewModel maintains the State even through configuration changes.
 */

@HiltViewModel
class CompanyListingsViewModel @Inject constructor(
    private val repository: StockRepository,
    private val savedStateHandle: SavedStateHandle,
    private val navigationUseCase: NavigationUseCase
) : ViewModel() {

    private var _state = MutableStateFlow(CompanyListingsState())
    val state = _state.asStateFlow() // Exposed read only value of state

    /**
     * This UseCase abstracts navigation events and centralizes navigation-related decisions.
     * The UseCase is being implemented by the NavigationCoordinator; calling navigation()
     * will invoke NavigationCoordinator.navigate()
     */
    fun onCompanySelected(symbol: String) {
        viewModelScope.launch {
            navigationUseCase.navigate(StockDestination.CompanyDetails(symbol))
        }
    }

    /**
     * Whenever we type anything in our search bar, we will automatically search. We should
     * add a slight delay so that we only search when the user has finished typing. That way we
     * don't search the database when the user types in every single letter or backspaces every
     * single letter.
     */
    private var searchJob: Job? = null

    /**
     * Lambda function for handling state changes. It operates on an instance of
     * CompanyListingsState. CompanyListingsState.() means we are using an extension function on
     * the CompanyListingsState class, which allows us to access the properties and methods of
     * CompanyListingsState directly inside the lambda (i.e. _state). The lambda takes
     * no parameters and instead returns a new CompanyListingState instance.
     *
     * It does the following:
     * 1. Accesses the current state (_state.value)
     * 2. Applies the update lambda in the argument to the current state
     * 3. Sets the state (_state.value) to the result of the lambda
     */
    private fun updateState(update: CompanyListingsState.() -> CompanyListingsState) {
        _state.value = _state.value.update()
    }

    /**
     * We receive events in our ViewModel with an onEvent function. After checking what type of
     * CompanyListingEvent it is, we can call the necessary actions we need to take for those
     * events.
     */
    fun onEvent(event: CompanyListingsEvent) {
        when (event) {
            is CompanyListingsEvent.Refresh -> {
                // When we're manually refreshing, as described by this event, we should set
                // fetchFromRemote = true.
                getCompanyListings(fetchFromRemote = true)
            }
            is CompanyListingsEvent.OnSearchQueryChanged -> {
                // Change the state value to the event's query
                updateState {
                   copy(searchQuery = event.query)
                }
                // If we already have a search job running, we should cancel it if we start a
                // new search. For example, if I type something, then .5s later I type another
                // letter, we shouldn't search twice; we should just cancel the first job and
                // wait until the user has completed typing in their search result before
                // actually making the getCompanyListings() request.
                debounceSearch { getCompanyListings() }
            }
        }
    }

    private fun debounceSearch(block: suspend () -> Unit) {
        searchJob?.cancel() // Cancel current search if it exists
        searchJob = viewModelScope.launch {
            delay(500L) // Wait 500ms before executing
            block()
        }
    }

    /**
     * Get all the companyListings based on the query, which is stored in the CompanyListingState,
     * and a value fetchFromRemote, which is by default false.
     *
     * Note that our query, despite being a constructor parameter, never needs to be passed in.
     * Thanks to stateful design, all we need to do is use the CompanyListingState, which we have a
     * reference to in our object, and get its current searchQuery and convert it to lowercase in
     * order to getCompanyListings(). This query value will be changed on the UI layer, but since
     * we'll be changing the value of the CompanyListingState as the user interacts with the UI,
     * we never need to pass this parameter in directly. Neat!
     */
    private fun getCompanyListings(
        query: String = state.value.searchQuery.lowercase(),
        fetchFromRemote: Boolean = false
    ) {
        // Launch a viewModelScope to call the suspend function that will give us the
        // CompanyListings from our StockRepository
        viewModelScope.launch {
            repository.getCompanyListings(query = query, fetchFromRemote = fetchFromRemote)
                // Collect the results. Since the result is a Resource<List<CompanyListing>>,
                // we want to use a when statement and determine our action based on the type
                // of the result.
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            // If the result for the search was a success, we should update the
                            // listings that our State currently holds in order to show a new
                            // view
                            result.data?.let { listings ->
                                updateState {
                                    copy(listings = listings)
                                }
                            }
                        }
                        is Resource.Error -> Unit
                        is Resource.Loading -> {
                            // Set loading to whatever was emitted by the result
                            updateState {
                                copy(isLoading = result.isLoading)
                            }
                        }
                    }
                }
        }
    }
}