package com.cabbage.mylibrary.place.model

import com.cabbage.mylibrary.GenericItem
import com.cabbage.mylibrary.GenericRes
import com.google.gson.annotations.SerializedName

data class AutoCompleteResponse(
        @SerializedName("predictions")
        override val results: List<PredictionsItem>? = null,
        override val status: String? = null,
        @SerializedName("error_message")
        override val errorMessage: String? = null
): GenericRes<PredictionsItem>

data class PredictionsItem(
        val description: String? = null,
        @SerializedName("place_id")
        val placeId: String? = null,
        val reference: String? = null,
        val types: List<String>? = null
): GenericItem

//data class StructuredFormatting(
//        @SerializedName("main_text")
//        val mainText: String? = null,
//        @SerializedName("secondary_text")
//        val secondaryText: String? = null,
//        @SerializedName("main_text_matched_substrings")
//        val mainTextMatchedSubstrings: List<MainTextMatchedSubstringsItem>? = null
//)

//data class MatchedSubstringsItem(
//        val offset: Int? = null,
//        val length: Int? = null
//)

//data class TermsItem(
//        val offset: Int? = null,
//        val value: String? = null
//)