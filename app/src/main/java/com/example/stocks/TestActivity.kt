package com.example.stocks

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.example.stocks.data.csv.CSVParser
import com.example.stocks.data.local.StockDatabase
import com.example.stocks.data.remote.StockApi
import com.example.stocks.domain.model.CompanyListing
import com.example.stocks.domain.repository.StockRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TestActivity : ComponentActivity() {

    @Inject lateinit var stockRepository: StockRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TestActivity", "StockRepository instance: $stockRepository")
    }
}