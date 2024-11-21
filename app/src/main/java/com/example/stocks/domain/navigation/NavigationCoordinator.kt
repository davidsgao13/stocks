package com.example.stocks.domain.navigation

import com.example.stocks.domain.use_case.navigation.NavigationUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationCoordinator @Inject constructor() : NavigationUseCase {
    private val _navigationEffects = MutableSharedFlow<NavigationEffect>()
    val navigationEffects = _navigationEffects.asSharedFlow()

    // Unused as we do not have tabs
//    private val _currentTab = MutableStateFlow<MainNavigation.Tab>(MainNavigation.Tab.Home)
//    val currentTab = _currentTab.asStateFlow()

    private var pendingNavigation: NavigationEffect? = null

    override suspend fun navigate(destination: NavigationDestination) {
        // Business logic for navigation
    }

    override suspend fun popBackStack(route: String?, inclusive: Boolean) {
        _navigationEffects.emit(NavigationEffect.PopBackStack(route, inclusive))
    }

    // Unused as we do not have tabs
//    override suspend fun switchTab(tab: MainNavigation.Tab) {
//        _currentTab.value = tab
//        _navigationEffects.emit(NavigationEffect.SwitchTab(tab))
//    }
}
