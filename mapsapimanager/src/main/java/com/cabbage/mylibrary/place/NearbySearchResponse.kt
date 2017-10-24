package com.cabbage.mylibrary.place

import com.cabbage.mylibrary.common.GenericItem
import com.cabbage.mylibrary.common.GenericRes
import com.cabbage.mylibrary.common.Geometry
import com.google.gson.annotations.SerializedName

data class NearbySearchResponse(
        override val results: List<NearbySearchItem>? = null,
        override val status: String? = null,
        @SerializedName("error_message")
        override val errorMessage: String? = null
) : GenericRes<NearbySearchItem>

data class NearbySearchItem(
        val name: String? = null,
        @SerializedName("place_id")
        val placeId: String? = null,
        val geometry: Geometry? = null,
        val vicinity: String? = null
) : GenericItem
