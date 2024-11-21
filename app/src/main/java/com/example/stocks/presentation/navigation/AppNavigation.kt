package com.example.stocks.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    navigationHandler: NavigationHandler,
) {
    LaunchedEffect(Unit) {
        navigationHandler.handleNavigationEffects(navController)
    }
    AppNavHost(navController = navController)
}