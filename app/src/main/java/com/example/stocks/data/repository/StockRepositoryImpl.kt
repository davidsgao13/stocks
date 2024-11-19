package com.example.stocks.data.repository

import com.example.stocks.data.csv.CSVParser
import com.example.stocks.data.local.StockDatabase
import com.example.stocks.data.mapper.toCompanyListing
import com.example.stocks.data.mapper.toCompanyListingEntity
import com.example.stocks.data.remote.StockApi
import com.example.stocks.domain.model.CompanyListing
import com.example.stocks.domain.repository.StockRepository
import com.example.stocks.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The actual functionality and caching logic for the StockRepository should be written here,
 * since caching logic will involve Room, and Room is data related.
 *
 * @Singleton states that we will only ever have one instance of this object used throughout our
 * entire app's lifecycle. This is required for injection. Furthermore, we can inject the
 * constructor parameters as well with dependency injection so that we don't need to ever
 * instantiate those classes more than once.
 *
 * We need the following objects for our StockRepositoryImpl:
 *    - We need our api to fetch remote data in the event the local cache is empty
 *    - We need our database to load data from the Dao
 *    - We need csv parsing
 *
 * We also need need to inherit the StockRepository interface that we described in the domain layer,
 * which means we need to implement the getListings() function
 */

@Singleton
class StockRepositoryImpl @Inject constructor(
    private val stockApi: StockApi,
    stockDatabase: StockDatabase,
    // Note that we are implementing CSVParser<CompanyListing> rather than CompanyListingsParser
    // because we want to depend on the abstraction and not the concretion. We just simply
    // pass in the CompanyListing object to the CSVParser, and since we wrote a
    // CompanyListingsParser that implements CSVParser<CompanyListing>, it will use that
    // functionality. If CompanyListingsParser were to ever change, since we're just using the
    // interface with CSVParser<CompanyListing>, we wouldn't need to change anything
    private val companyListingsParser: CSVParser<CompanyListing>
) : StockRepository {

    /**
     * Allows us to get all the CompanyListings from the database.
     */
    private val dao = stockDatabase.dao

    /**
     * We need to return a Flow<Resource<List<CompanyListing>>> object. To do so, we start with:
     *
     * - return flow { } in order to return a Flow object
     * - emit() tells our Flow to put out the values we've described. These values will be captured
     *   by the ViewModel, which will then alter the View based on the values we've emitted.
     *
     *   We start by emitting Resource.Loading(true) to let our ViewModel to know that we're
     *   loading. Remember, since Flows can emit multiple values over a period of time, we can
     *   call this emit() function and then call emit() later for other values we want to output.
     *   emit(Resource.Loading(true)) tells our ViewModel that we're about to start a database
     *   query.
     *
     *   Next, we retrieve localListings by calling searchCompanyListing(query) in our database dao.
     *   We provided the query as a constructor variable for getCompanyListings().
     *
     *   Now that we've retrieved our localListings, we want to emit a Success value from the Flow,
     *   along with the data (localListings) that we want to send over. However, localListings is
     *   not of our expected output type List<CompanyListing>, but rather a
     *   List<CompanyListingEntity>. In order for us to send over the proper output type, we
     *   utilize the method we wrote in CompanyMapper toCompanyListing(), which converts a single
     *   CompanyListingEntity into a CompanyListing object for our frontend to use. We call
     *   localListings.map { } in order to apply this transformation on every CompanyListingEntity
     *   inside of localListings. By calling localListings.map { it.toCompanyListing() }, we can
     *   individually convert each CompanyListingEntity inside localListings into a CompanyListing.
     *   This converts our List<CompanyListingEntity> to a List<CompanyListing> and resolves our
     *   type mismatch problem.
     *
     *   We also need to write a case for when to fetch remote data from the API. For that reason,
     *   we create a Boolean value isDbEmpty and set it to localListings.isEmpty() &&
     *   query.isBlank().This is because we could potentially have an empty localListings object
     *   if our query is not blank, but we simply searched for a listing that doesn't exist
     *   (e.g. if we typed in 'ZZZZZZZ' we'd get nothing back from localListings since no
     *   CompanyListing has a name or symbol that matches 'ZZZZZZZ.' However, we don't want to
     *   query the remote database for this scenario of localListings.isEmpty(); we only want
     *   to query it if the database is initially empty. Thus, we add this second check to see
     *   if the query.isBlank(), which means we're currently querying for everything in our local
     *   database, and localListings is still empty, which means that there is nothing in our
     *   local database.
     *
     *   We calculate whether we should load from the cache if the database is empty and if it is
     *   not specified that we should fetchFromRemote. Otherwise, we end the flow's process
     *   with return@flow.
     *
     */
    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {
        // Return a flow builder, which takes in a lambda function and constructs a
        // Flow<Resource<List<CompanyListing>>>. The lambda is the entire { ... } block, which
        // serves as a set for emitting values
        return flow {
            // Emit a loading state
            emit(Resource.Loading(true))
            val localListings = dao.searchCompanyListing(query)
            // Emit cached data after converting it from a DTO
            emit(Resource.Success(data = localListings.map { it.toCompanyListing() }))

            // Check we should load from cache if db is empty and we don't specify a remote fetch
            val isDbEmpty = localListings.isEmpty() && query.isBlank()
            val shouldLoadFromCache = !isDbEmpty && !fetchFromRemote
            if (shouldLoadFromCache) {
                // Emit that we've finished loading
                emit(Resource.Loading(false))
                // Exits flow { } block since we're going to fetch data locally instead of remotely.
                // return@flow doesn't terminate getCompanyListings(). It only terminates the
                // lambda, which in this case is the entire { ... } block that is passed into the
                // flow builder as an argument.
                return@flow
            }
            val remoteListings = try {
                // Use our stockApi to getListings()
                val response = stockApi.getListings()
                // Pass the response.byteStream() InputStream to the parser in order to parse
                // the result of our response
                companyListingsParser.parse(response.byteStream())
            } catch (e: IOException) {
                // IOException fires if something goes wrong with parsing the data
                e.printStackTrace()
                emit(Resource.Error(message = "Couldn't load data."))
                null
            } catch (e: HttpException) {
                // HttpException fires if the response is a failure (e.g. 400).
                e.printStackTrace()
                emit(Resource.Error(message = "Response failure."))
                null
            }
            // If remoteListings isn't null
            remoteListings?.let { listings ->
                // Clear the database, then add the returned listings as entities in the database
                dao.clearCompanyListings()
                dao.insertCompanyListings(listings.map { it.toCompanyListingEntity() })
                // Then return a Success with all the listings attached for future use and emit
                // Resource.Loading(false) to let whatever is observing the flow know that we have
                // finished loading.
                emit(Resource.Success(
                    // We do this in order to stick to the single source of truth principle.
                    // We only  want to get data from the database; we shouldn't pass data
                    // directly from the API to the UI. Instead, what we should do is take the data
                    // from the API, insert it into the database, and then query the database so
                    // that we're loading from the database. If we had written:
                    //
                    // emit(Resource.Success(listing))
                    //
                    // Then we wouldn't be adhering to the single source of truth principle, since
                    // we're directly emitting data retrieved from the API rather than retrieving
                    // it from the database, which should be our source of truth.

                    data = dao.searchCompanyListing("").map { it.toCompanyListing() }
                ))
                emit(Resource.Loading(false))
            }
        }
    }

}