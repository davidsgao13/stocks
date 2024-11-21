package com.example.stocks.presentation.company_listings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

/**
 * In this case, we're going to use a ComposeNavigator to align with the native Compose
 * navigation library, leveraging 2.8.0's new type-safety features. In this case, we want
 * to add the ViewModel as part of the Screen to adhere to clean architecture; we don't want
 * to handle navigation, which is a UI event, in the ViewModel. The ViewModel should only emit
 * State, it shouldn't actually control navigation between views.
 */

@Composable
fun CompanyListingsScreen(
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
    }
}