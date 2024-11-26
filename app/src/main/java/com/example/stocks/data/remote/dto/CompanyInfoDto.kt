package com.example.stocks.data.remote.dto

import com.squareup.moshi.Json

/**
 * A DTO is a Data Transfer Object, used to present the Kotlin representation of a JSON
 * response.
 */

data class CompanyInfoDto(
    // This is the equivalent of using @SerializedName with the Moshi library. We use the
    // @field:Json annotation to signify that we will be using a JSON specific name, then we include
    // the argument 'name = xxx', where xxx is the name of the JSON field. In this case, our field's
    // name is "Symbol", but since we don't want to keep capitalized field names in our DTO, we
    // convert that to "name" in the DTO.
    @field:Json(name = "Symbol") val symbol: String?,
    @field:Json(name = "Description") val description: String?,
    @field:Json(name = "Name") val name: String?,
    @field:Json(name = "Country") val country: String?,
    @field:Json(name = "Industry") val industry: String?
)