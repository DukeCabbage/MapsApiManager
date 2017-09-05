package com.cabbage.mylibrary.place

import com.cabbage.mylibrary.place.model.AutoCompleteResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface PlaceApi {

    @GET(PATH_SEARCH)
    fun autoComplete(@Query("input") input: String,
                     @Query("location") location: String,
                     @Query("radius") radius: Int,
                     @QueryMap(encoded = false) optional: Map<String, String>?)
            : Observable<AutoCompleteResponse>

    //    @GET(PATH_DETAIL)
    //    fun getDetailByPlaceId(@Query("place_id") placeId: String,
    //                           @QueryMap(encoded = false) optional: Map<String, String>?)
    //
    //    @GET(PATH_DETAIL)
    //    fun getDetailByReference(@Query("reference") reference: String,
    //                             @QueryMap(encoded = false) optional: Map<String, String>?)

    companion object {
        private const val PATH_SEARCH = "maps/api/place/autocomplete/json"

        private const val PATH_DETAIL = "maps/api/place/details/json"
    }
}