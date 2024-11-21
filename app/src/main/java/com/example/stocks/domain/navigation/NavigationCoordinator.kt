package com.example.stocks.domain.navigation

import com.example.stocks.domain.use_case.navigation.NavigationUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationCoordinator @Inject constructor() : NavigationUseCase {

    /**
     * The reason we're using a SharedFlow is because navigation perfectly aligns with a
     * actor-model paradigm. It decouples the event producers (NavigationCoordinator) and consumers
     * (NavigationHandler). The ViewModel is abstracted from this entire process, as all it does
     * is give the command to the NavigationCoordinator to emit the navigation event; it isn't
     * actually emitting anything itself, rather, it's delegating this to the NavigationCoordinator.
     *
     * Here, the NavigationCoordinator is collecting these events, translating them into
     * NavigationEffects, and emitting them to the SharedFlow, which will be consumed by the
     * NavigationHandler.
     */
    private val _navigationEffects = MutableSharedFlow<NavigationEffect>()
    val navigationEffects = _navigationEffects.asSharedFlow()

    // Unused as we do not have tabs
//    private val _currentTab = MutableStateFlow<MainNavigation.Tab>(MainNavigation.Tab.Home)
//    val currentTab = _currentTab.asStateFlow()

    private var pendingNavigation: NavigationEffect? = null

    /**
     * The Coordinator is emitting the NavigationEffect based on the NavigationDestination passed
     * by the CompanyListingsViewModel; it does this because the Coordinator implements from the
     * NavigationUseCase abstraction. Since the UseCase is used by the ViewModel to separate
     * concerns with the UI layer, the Coordinator is the actual class that is handling the
     * emissions rather than the ViewModel. The ViewModel is just calling the Coordinator to
     * send those emissions. The NavigationHandler is observing all emissions, and whenever
     * an emission is actually sent, it sends the data over to the NavigationController to handle
     * the actual process of navigation.
     */
    override suspend fun navigate(destination: NavigationDestination) {
        _navigationEffects.emit(NavigationEffect.Navigate(destination))
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
