package com.cabbage.mylibrary.place

import com.cabbage.mylibrary.common.GenericItem
import com.cabbage.mylibrary.common.GenericRes
import com.google.gson.annotations.SerializedName

data class AutoCompleteResponse(
        @SerializedName("predictions")
        override val results: List<PredictionsItem>? = null,
        override val status: String? = null,
        @SerializedName("error_message")
        override val errorMessage: String? = null
) : GenericRes<PredictionsItem>

data class PredictionsItem(
        val description: String? = null,
        @SerializedName("place_id")
        val placeId: String? = null,
        val reference: String? = null,
        val types: List<String>? = null,
        @SerializedName("structured_formatting")
        val structuredFormatting: StructuredFormatting? = null
) : GenericItem

data class StructuredFormatting(
//        @SerializedName("main_text_matched_substrings")
//        val mainTextMatchedSubstrings: List<MainTextMatchedSubstringsItem>? = null,
        @SerializedName("secondary_text")
        val secondaryText: String? = null,
        @SerializedName("main_text")
        val mainText: String? = null
)

//data class MainTextMatchedSubstringsItem(
//        val offset: Int? = null,
//        val length: Int? = null
//)