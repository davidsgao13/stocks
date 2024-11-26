package com.example.stocks.data.mapper

import com.example.stocks.data.local.CompanyListingEntity
import com.example.stocks.data.remote.dto.CompanyInfoDto
import com.example.stocks.domain.model.CompanyInfo
import com.example.stocks.domain.model.CompanyListing

/**
 * The purpose of this file is to create extension functions for us to convert from a
 * CompanyListingEntity data value into a CompanyListing value to be used in the domain. Since
 * the domain layer is the inner most layer in clean architecture, it  should be accessible from
 * every other layer in the application. That's why it's okay to access CompanyListing (a domain
 * object) from the data layer.
 */

fun CompanyListingEntity.toCompanyListing() : CompanyListing {
    return CompanyListing(
        name = name,
        symbol = symbol,
        exchange = exchange
    )
}

/**
 * Since the id for the CompanyListingEntity is automatically generated thanks to having the
 * @PrimaryKey annotation provided to us by the Room library, we don't need to pass that in as
 * an argument for the CompanyListingEntity constructor.
 */
fun CompanyListing.toCompanyListingEntity() : CompanyListingEntity {
    return CompanyListingEntity(
        name = name,
        symbol = symbol,
        exchange = exchange
    )
}

fun CompanyInfoDto.toCompanyInfo() : CompanyInfo {
    return CompanyInfo(
        // Need to make a null check in case any of these values are null in the event that we
        // hit a quota. These fields aren't included in every response once we've hit our quota, so
        // we need to ensure that we return a non-null value.
        symbol = symbol ?: "",
        description = description ?: "",
        name = name ?: "",
        country = country ?: "",
        industry = industry ?: ""
    )
}