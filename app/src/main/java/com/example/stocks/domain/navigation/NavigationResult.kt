package com.example.stocks.domain.navigation

sealed interface NavigationResult<T> {
    data class Success<T>(val data: T) : NavigationResult<T>
    class Cancelled<T> : NavigationResult<T>
}