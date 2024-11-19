package com.example.stocks.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * The Dao (Data Access Object) is another interface where we define how we want to interact with
 * our local database. Queries like insert and delete, and what we do with those queries,
 * are defined here.
 */

@Dao
interface StockDao {

    /**
     * @Insert lets us tell Room to perform an operation in the event of inserting a record into
     * the Dao, specifically when that insertion causes a conflict. In this case, we're going to
     * REPLACE the value if has the same PrimaryKey value. This makes sense since we want to
     * override any of the changes
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompanyListings(
        companyListingEntities: List<CompanyListingEntity>
    ) {
    }

    /**
     * @Query allows us to write raw SQL queries for custom deletion operations. Since it supports
     * complex operations and conditions, it is more flexible that a standard @Delete
     * @Delete is more so designed for deleting specific entity objects (e.g. deleting a record
     * based on its id) rather than deleting an entire table. @Delete operates on entity objects,
     * so it requires us to load all a table's rows as entity objects before deleting them. This
     * is inefficient when deleting an entire table; it is much easier to simply reference the
     * table by name using a custom @Query and delete it. Since it works directly on the database
     * level, @Query is generally more efficient overall as well.
     */
    @Query("DELETE FROM CompanyListingEntity")
    suspend fun clearCompanyListings()

    /**
     * This is a more complex SQLite query that we can write thanks to Room. We use """ in order to
     * establish that we are writing a raw SQL query.
     *
     * SELECT * returns all the columns from CompanyListingEntity
     * FROM determines which table (CompanyListingEntity) we are fetching
     *
     * The WHERE clause is slightly more in depth. We're not only querying for the name of the
     * company in our search, but we're also querying for the company's symbol. This means, for
     * example, if we search TES, then we'll find Tesla. Same if we search for TSL, as the symbol
     * for Tesla is TSLA.
     *
     * First condition: LOWER(name) LIKE '%' || LOWER(:query) || '%'
     * - LOWER(name) converts the name column to lowercase for a case-insensitive comparison
     * - LIKE performs a partial match (substring search)
     * - LOWER(:query) converts the query to lowercase for a case-insensitive comparison
     * - '%' act as wildcards for matching any number of characters before (% at the start)
     *    and any number of characters after (% at the end)
     * - || acts as concatenation in Sqlite, as explained above.
     *
     * So really, what we're doing is matching '%tes%' when the user types 'tes' into the search bar
     * Final result: Matches rows where name contains the searched query String
     *
     * Second condition: UPPER(:query) == symbol
     * - Compares whether the searched query, when converted to uppercase, exactly matches the
     *    symbol (e.g. TSLA for Tesla)
     * Final result: Matches rows where the search is exactly the name of a company's symbol
     *
     * Since we have the OR in between both queries, we're searching for either of these conditions
     */
    @Query(
        """
            SELECT *
            FROM CompanyListingEntity
            WHERE LOWER(name) LIKE '%' || LOWER(:query) || '%' OR
                UPPER(:query) == symbol
        """
    )
    suspend fun searchCompanyListing(query: String) : List<CompanyListingEntity>

}