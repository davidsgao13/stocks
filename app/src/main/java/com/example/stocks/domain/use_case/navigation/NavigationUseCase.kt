package com.example.stocks.domain.use_case.navigation

import com.example.stocks.domain.navigation.NavigationDestination

interface NavigationUseCase {
    suspend fun navigate(destination: NavigationDestination)
    suspend fun popBackStack(route: String? = null, inclusive: Boolean = false)
    //If we used tabs, we'd implement this too
//    suspend fun switchTab(tab: MainNavigation.Tab)
}
