package com.example.stocks.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @Entity annotation tells Room that this is a table in our database.
 *
 * @PrimaryKey tells Room that this field will be our table's unique primary key. This will let
 * Room automatically generate unique identifiers for us, specifically for the id field.
 */

@Entity
data class CompanyListingEntity(
    val name: String,
    val symbol: String,
    val exchange: String,
    @PrimaryKey val id: Int? = null
)
