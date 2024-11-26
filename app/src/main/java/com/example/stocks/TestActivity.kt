package com.example.stocks

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Text
import com.example.stocks.data.csv.CSVParser
import com.example.stocks.data.local.StockDatabase
import com.example.stocks.data.remote.StockApi
import com.example.stocks.domain.model.CompanyListing
import com.example.stocks.domain.repository.StockRepository
import com.example.stocks.presentation.company_listings.CompanyListingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TestActivity : ComponentActivity() {

    // DO NOT DO THIS:
    // @Inject lateinit var viewModel: CompanyListingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve ViewModel via the correct method
        val viewModel: CompanyListingsViewModel by viewModels()
        Log.d("TestActivity", "ViewModel: $viewModel")
    }
}