package com.example.stocks.data.remote

import com.example.stocks.BuildConfig
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface StockApi {

    @GET("query?function=LISTING_STATUS")
    suspend fun getListings(@Query("apikey") apiKey: String = BuildConfig.API_KEY): ResponseBody
}