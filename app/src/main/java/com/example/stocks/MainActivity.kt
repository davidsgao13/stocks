package com.example.stocks

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.stocks.presentation.company_listings.NavGraphs
import com.example.stocks.ui.theme.StocksTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import dagger.hilt.android.AndroidEntryPoint

/**
 * @AndroidEntryPoint allows us to inject dependencies into Android components, like Activities.
 * This will be used for us to inject the ViewModel into the Compose code.
 */

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StocksTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    /**
                     * Sets up a navigation host for the app with the Destinations library.
                     * It acts as the main entry point for the library. The DestinationsNavHost
                     * defines the root navigation host, which is responsible for rendering screens
                     * based on the passed in routes and handling navigating between destinations.
                     *
                     * @NavGraphs.root is a generated object, specifically the startingDestination
                     * for the Destinations library.
                     */
                    DestinationsNavHost(navGraph = NavGraphs.root )
                }
            }
        }
    }
}