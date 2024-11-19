package com.example.stocks.data.local

import androidx.room.Database

/**
 * @Database annotation is necessary for Room to understand that this is a Room database.
 * @entities are a list of tables; in this case, we only have a single table, CompanyListingEntity.
 * @version = 1 means that we have set our database version to 1; we increment this whenever we
 * make changes to the database
 */
@Database(
    entities = [CompanyListingEntity::class],
    version = 1
)
abstract class StockDatabase() {
    abstract val dao: StockDao
}