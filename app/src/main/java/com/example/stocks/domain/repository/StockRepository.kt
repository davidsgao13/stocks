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
     *
     * Note 1: if there is a query that enables real-time search, caching to combine local and remote
     * data sources, multiple emissions over time in the form of streaming to represent dynamic
     * updates, or loading as a function of the UI, you should use a Flow
     *
     * Note 2: Flows greatly assist with backpressure, which occurs when a producer, which is a
     * source of data (API, database), emits data faster than the consume, the part of the app
     * that processes the data (database, UI component, file writer) can consume it. There is a risk
     * of resource exhaustion if this occurs. This type of issue happens because consumers might
     * need to take time to process each item due to operations like I/O, computation, or rendering
     * delays. Flows ensure that the producer adjusts its emission rate to align with the consumer's
     * capacity. You can do this with calls like .buffer(), which temporarily stores values so that
     * the producer doesn't have to wait for the consumer to be ready in order to keep producing
     * values. This is very helpful in streaming. Other helpful operations include:
     * - debounce(), which emits the most recent value after a delay, removing intermediate results
     * - conflate(), which only keeps the latest value when the consumer is slow
     * - sample(), which emits a value at a decided, fixed interval
     */

    suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ) : Flow<Resource<List<CompanyListing>>>

    /**
     * Similar logic for these two network calls; we will be providing a symbol and retrieving both
     * the IntradayInfo and the CompanyInfo objects from that symbol. We wrap both of these in
     * a Resource, since we want to handle the different states of the request (e.g. Error, Success,
     * Loading). However, we don't wrap these methods in a Flow, because these are one-shot events
     * that return a single value. Unlike getCompanyListings, which is a request we may want to have
     * a continuous connection to, we only return one value per request, so we only ever need to
     * return a Resource.
     *
     * Note: despite getIntradayInfo returning a List, which can contain multiple values, it is
     * still a single value that happens to contain multiple items, whereas in getCompanyListings,
     * we want to get live updates to the company listings, so we return it as a flow. In this case
     * the CompanyInfoScreen is static once the user arrives on the page and doesn't make further
     * query checks for updates.
     */
    suspend fun getIntradayInfo(
        symbol: String
    ) : Resource<List<IntradayInfo>>

    suspend fun getCompanyInfo(
        symbol: String
    ) : Resource<CompanyInfo>
}