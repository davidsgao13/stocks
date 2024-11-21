package com.example.stocks.presentation.navigation

import androidx.navigation.NavHostController
import com.example.stocks.domain.navigation.NavigationCoordinator
import com.example.stocks.domain.navigation.NavigationEffect
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationHandler @Inject constructor(
    private val navigationCoordinator: NavigationCoordinator
) {
    suspend fun handleNavigationEffects(navController: NavHostController) {
        navigationCoordinator.navigationEffects.collect { effect ->
            when (effect) {
                is NavigationEffect.Navigate -> {
                    navController.navigate(effect.destination) {
                        effect.navOptions?.let { options ->
                            options.popUpTo?.let { route ->
                                popUpTo(route) { inclusive = options.inclusive }
                            }
                            launchSingleTop = options.singleTop
                            restoreState = options.restoreState
                        }
                    }
                }
                is NavigationEffect.PopBackStack -> {
                    if (effect.route != null) {
                        navController.popBackStack(
                            route = effect.route,
                            inclusive = effect.inclusive
                        )
                    } else {
                        navController.navigateUp()
                    }
                }
                // Unused as we do not have tabs in this project
//                is NavigationEffect.SwitchTab -> {
//                    navController.navigate(effect.tab.route) {
//                        popUpTo(navController.graph.findStartDestination().id) {
//                            saveState = effect.saveState
//                        }
//                        launchSingleTop = true
//                        restoreState = effect.saveState
//                    }
//                }
            }
        }
    }
}
