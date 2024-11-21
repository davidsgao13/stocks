package com.example.stocks.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.stocks.domain.navigation.StockDestination
import com.example.stocks.presentation.company_listings.CompanyListingsScreen

/**
 * The AppNavHost function acts as the navigation graph in Jetpack Compose using the native
 * navigation library. It uses a navHostController, which manages the navigation stack and
 * facilitates navigation between screens.
 *
 * @rememberNavController() function creates and remembers an instance of NavHostController that
 * will maintain its value during recomposition.
 */

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = StockDestination.CompanyListings
    ) {
        composable<StockDestination.CompanyListings> {
            CompanyListingsScreen()
        }

        composable<StockDestination.CompanyDetails> { backStackEntry ->
            val data = backStackEntry.toRoute<StockDestination.CompanyDetails>()
//            CompanyDetailsScreen(symbol = data.symbol)
        }
    }
}