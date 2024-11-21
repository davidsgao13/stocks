package com.example.stocks.domain.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface NavigationDestination {
    val route: String
    val requiresAuth: Boolean get() = false
    val requiredScopes: Set<String> get() = emptySet()
}