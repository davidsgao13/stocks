package com.example.stocks.di

import com.example.stocks.data.csv.CSVParser
import com.example.stocks.data.csv.CompanyListingsParser
import com.example.stocks.data.csv.IntradayInfoParser
import com.example.stocks.data.repository.StockRepositoryImpl
import com.example.stocks.domain.model.CompanyListing
import com.example.stocks.domain.model.IntradayInfo
import com.example.stocks.domain.repository.StockRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * We should create a separate module for our abstract injections.
 */

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Note that normally we would annotate an injection with @Provides, but in the case of
     * abstract injections, we should use @Binds in order to provide an abstraction to
     * eliminate boiler plate code. By specifying the return type of CSVParser<CompanyListing>,
     * Dagger will know how to inject this abstraction that is currently being used in the
     * companyListingsParser.
     */
    @Binds
    @Singleton
    abstract fun bindCompanyListingsParser(
        companyListingsParser: CompanyListingsParser
    ): CSVParser<CompanyListing>

    /**
     * Same exact logic as the CompanyListingsParser
     */

    @Binds
    @Singleton
    abstract fun bindIntradayInfoParser(
        intradayInfoParser: IntradayInfoParser
    ) : CSVParser<IntradayInfo>

    /**
     * Same logic here -- we want to inject StockRepositoryImpl, since we're trying to use its
     * implementation of getCompanyListings(), but in actuality, we need to inject the abstraction
     * so that Hilt can inject the implementation.
     */
    @Binds
    @Singleton
    abstract fun bindStockRepository(
        stockRepository: StockRepositoryImpl
    ): StockRepository
}