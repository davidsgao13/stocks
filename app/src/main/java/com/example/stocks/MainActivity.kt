package com.example.stocks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.stocks.presentation.navigation.AppNavigation
import com.example.stocks.ui.theme.StocksTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StocksTheme {
                // We want to abstract the navigation code to a separate class (a Composable
                // function) to keep the MainActivity focused on high-level app setup. In this case,
                // we're going to abstract to AppNavigation, which acts as the main navigation
                // container for the application. It manages both the UI and the navigation state
                AppNavigation()
            }
        }
    }
}