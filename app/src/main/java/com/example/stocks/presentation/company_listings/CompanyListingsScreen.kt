package com.example.stocks.presentation.company_listings

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.stocks.presentation.destinations.CompanyInfoScreenDestination
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

/**
 * In this case, we're going to use the destinations compose library to align with native
 * Compose, leveraging 2.8.0's new type-safety features. It allows us to easily manage
 * navigation in Jetpack compose, no longer needing routes; the @Destination annotation will take
 * care of parameters and provide parameters in a type-safe manner.
 *
 * In this case, we also want to add the ViewModel as part of the Screen to adhere to clean
 * architecture; we don't want to handle navigation, which is a UI event, in the ViewModel.
 * The ViewModel should only emit. State, it shouldn't actually control navigation between views.
 */

@Composable
@Destination(start = true)
fun CompanyListingsScreen(
    navigator: DestinationsNavigator,
    viewModel: CompanyListingsViewModel = hiltViewModel()
) {
    // Use collectAsState to observe the state flow, since state is a MutableStateFlow() and not
    // a MutableState; we therefore need to convert this into a State by calling collectAsState().
    // When state.value changes, collectAsState() will notify Compose to recompose the
    // CompanyListingsScreen. We do this because state.value does not inherently trigger
    // recomposition in Jetpack Compose. If isRefreshing changes, Compose won’t recompose because
    // it isn’t observing StateFlow. This is why we should instantiate the state as
    // viewModel.state.collectAsState() instead, making our UI reactive. Whenever the StateFlow
    // emits a new value, because our UI is reactive, recomposition will occur automatically.
    val state by viewModel.state.collectAsState()
    val swipeRefreshState = rememberSwipeRefreshState(
        isRefreshing = state.isRefreshing
    )
    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            // Create a placeholder Text composable to be displayed when the text field is in focus
            // and the input text is empty. The default text style for internal Text is
            // Typography.Large
            placeholder = {
                Text(text = "Search...")
            },
            maxLines = 1,
            singleLine = true,
            value = state.searchQuery,
            onValueChange = {
                /**
                 * Whenever the value of the search query changes, we want to send this event
                 *.to the ViewModel in order to perform business logic on the input.
                 * Thh value comes from our state (state.searchQuery) and goes to the ViewModel.
                 *
                 * Step-by-step Flow of Events
                 * Here’s what happens when the user types:
                 *
                 * The user types a character, triggering onValueChange.
                 * The new text is passed as it to the onValueChange lambda.
                 * The lambda sends an event to the ViewModel, e.g.
                 */
                viewModel.onEvent(CompanyListingsEvent.OnSearchQueryChanged(it))
            }
        )
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                viewModel.onEvent(CompanyListingsEvent.Refresh)
            }
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                // Normally we would use items(state.listings) to show all of the listings; however,
                // we're going to be applying specific view logic just above the LazyColumn since
                // we want to create a horizontal divider to visually separate the LazyColumn
                // from the search bar.
                items(state.listings.size) { index ->
                    val company = state.listings[index]
                    CompanyItem(
                        company = company,
                        // Ensures the whole CompanyItem stretches across the row by passing in
                        // the fillsMaxWidth() modifier to the CompanyItem.
                        modifier = Modifier.fillMaxWidth()
                            .clickable {
                                // Clicking on the CompanyItem should navigate to the detail screen
                                navigator.navigate(
                                    CompanyInfoScreenDestination(company.symbol)
                                )
                                //TODO: Navigate to detail screen
                            }
                            .padding(16.dp)
                    )
                    // Create padding between each item on the list, except for the last item
                    if (index < state.listings.size) {
                        HorizontalDivider(modifier = Modifier.padding(
                            horizontal = 16.dp
                        ))
                    }
                }
            }
        }
    }
}