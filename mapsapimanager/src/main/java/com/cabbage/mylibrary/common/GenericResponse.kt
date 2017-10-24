package com.cabbage.mylibrary.common

import com.google.gson.annotations.SerializedName

interface GenericRes<out GenericItem> {
    val results: List<GenericItem>?
    val status: String?
    val errorMessage: String?
}

interface GenericItem

data class AddressComponent(
        val types: List<String>? = null,
        @SerializedName("short_name")
        val shortName: String? = null,
        @SerializedName("long_name")
        val longName: String? = null
)

data class Geometry(
        val viewport: Bounds? = null,
        val bounds: Bounds? = null,
        val location: Location? = null,
        @SerializedName("location_type")
        val locationType: String? = null
)

data class Bounds(
        val southwest: Location? = null,
        val northeast: Location? = null
)

data class Location(
        val lng: Double? = null,
        val lat: Double? = null
)