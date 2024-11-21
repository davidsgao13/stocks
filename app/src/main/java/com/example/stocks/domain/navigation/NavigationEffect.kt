package com.example.stocks.domain.navigation

sealed interface NavigationEffect {
    data class Navigate(
        val destination: NavigationDestination,
        val navOptions: NavOptionsEffect? = null
    ) : NavigationEffect

    data class PopBackStack(
        val route: String? = null,
        val inclusive: Boolean = false
    ) : NavigationEffect

    // Unneeded because this app doesn't use tabs for navigation events
//    data class SwitchTab(
//        val tab: MainNavigation.Tab,
//        val saveState: Boolean = true
//    ) : NavigationEffect
}