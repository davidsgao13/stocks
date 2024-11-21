package com.example.stocks.domain.navigation

sealed interface NavigationEvent {
    val destination: NavigationDestination
}