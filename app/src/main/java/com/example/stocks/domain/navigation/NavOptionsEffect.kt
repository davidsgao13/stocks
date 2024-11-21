package com.example.stocks.domain.navigation

data class NavOptionsEffect(
    val popUpTo: String? = null,
    val inclusive: Boolean = false,
    val singleTop: Boolean = false,
    val restoreState: Boolean = false
)
