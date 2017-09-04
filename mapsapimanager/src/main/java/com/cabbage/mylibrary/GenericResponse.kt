package com.cabbage.mylibrary

//abstract class GenericResponse<out GenericItem>(
//    val results: List<GenericItem>? = null,
//    val status: String? = null,
//    @SerializedName("error_message")
//    val errorMessage: String? = null
//)

interface GenericRes<out GenericItem> {
    val results: List<GenericItem>?
    val status: String?
    val errorMessage: String?
}

interface GenericItem