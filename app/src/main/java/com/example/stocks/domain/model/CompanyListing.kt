package com.example.stocks.domain.model

/**
 * By excluding the id field, we ensure that this class has nothing to do with any third-party id,
 * like Room, which will enumerate the PrimaryKey id. This gives us an advantage if we ever decide
 * to change to a different database library, we don't need to make any changes to domain level
 * models, since they're completely independent of third-party libraries. This means we only need
 * to change the DTO objects. We want to avoid using data logic (e.g. @Entity) in the domain layer.
 */

data class CompanyListing(
    val name: String,
    val symbol: String,
    val exchange: String
)