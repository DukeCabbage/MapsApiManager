package com.cabbage.mylibrary.place

import com.cabbage.mylibrary.common.AddressComponent
import com.cabbage.mylibrary.common.GenericItem
import com.cabbage.mylibrary.common.GenericRes
import com.cabbage.mylibrary.common.Geometry
import com.google.gson.annotations.SerializedName

data class PlaceDetailResponse(
        val result: PlaceDetailItem? = null,
        override val status: String? = null,
        @SerializedName("error_message")
        override val errorMessage: String? = null
) : GenericRes<PlaceDetailItem> {
    // Place detail is bit special as the response contains a single item not a list of items
    override val results: List<PlaceDetailItem>?
        get() = null
}

data class PlaceDetailItem(
        @SerializedName("address_components")
        val addressComponents: List<AddressComponent>? = null,
        @SerializedName("formatted_address")
        val formattedAddress: String? = null,
        val geometry: Geometry? = null,
        @SerializedName("place_id")
        val placeId: String? = null,
        val name: String? = null,
        @SerializedName("permanently_closed")
        val permanentlyClosed: Boolean? = null,
        val rating: Float? = null,
        val types: List<String>? = null,
        val website: String? = null
) : GenericItem