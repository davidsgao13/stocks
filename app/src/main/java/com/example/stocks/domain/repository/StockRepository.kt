package com.example.stocks.domain.repository

import com.example.stocks.domain.model.CompanyInfo
import com.example.stocks.domain.model.CompanyListing
import com.example.stocks.domain.model.IntradayInfo
import com.example.stocks.util.Resource
import kotlinx.coroutines.flow.Flow

/**
 * This interface represents the functions that our repository needs to implement. The actual
 * implementation of this interface occurs in the data layer's repository. The presentation
 * layer will call this interface rather than the data layer's repository, StockRepositoryImpl.
 */

interface StockRepository {

    /**
     * @getCompanyListings should return a List of CompanyListings for us to consume locally.
     *
     * Since we're in the domain layer, we can only access the domain layer CompanyListing object.
     * We should not retrieve the CompanyListingEntity, which is a data layer object. All layers
     * should be able to call objects from the domain layer, but the domain layer is the inner
     * most class and should not call other layers.
     *
     * Note: since we want to distinguish between successes and errors returned from the API, we
     * wrap List<CompanyListing> with Resource<> so that if there's an error in trying to retrieve
     * the API, Resource will return an Error and we can derive a message from it. If it's
     * successful, we can just retrieve a List of CompanyListing.
     *
     * Lastly, since we're implementing a local cache with getCompanyListings, we want to wrap
     * all of it with Flow<>.
     *
     * Since a Flow is a coroutine that emits values over a period of time,
     * it fits our use case perfectly. This is because before we even load from the database, we
     * want to tell the ViewModel to show a progress indicator. We do this by setting the Resource
     * to Loading, then sending that Resource into the Flow.
     *
     * After that, we load the data from our local cache, and if retrieval is successful, we want
     * to set Resource to Success and request new data in the event there is an update or to
     * populate our database for the first time. Due to the multiple potential states the data can
     * exist in, as well as multiple results, we want to use a Flow since we want to emit all these
     * states at different times and reflect UI changes based on those states
     * (e.g. show progress bar if state is Loading).
     *
     * @fetchFromRemote is a Boolean that retrieves the remote data if set to true; this is for
     * forcing the function to retrieve the data in the event we manually refresh. If the Boolean
     * is set to false, then we'll simply retrieve data from the cache.
     *
     * @query is just the String query that we'll be inputting into the search text when searching
     * for company listings.
     */

    suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ) : Flow<Resource<List<CompanyListing>>>

    /**
     * Similar logic for these two network calls; we will be providing a symbol and retrieving both
     * the IntradayInfo and the CompanyInfo objects from that symbol. We wrap both of these in
     * a Resource, since we want to handle the different states of the request (e.g. Error, Success,
     * Loading, etc.)
     */
    suspend fun getIntradayInfo(
        symbol: String
    ) : Flow<Resource<List<IntradayInfo>>>

    suspend fun getCompanyInfo(
        symbol: String
    ) : Flow<Resource<CompanyInfo>>
}