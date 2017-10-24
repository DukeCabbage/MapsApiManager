package com.cabbage.mylibrary.place

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface PlaceApi {

    //region Place Search

    @GET("$PATH_SEARCH?rankby=prominence")
    fun nearbySearch(@Query("location") location: String,
                     @Query("radius") radius: Int,
                     @QueryMap optional: Map<String, String>?)
            : Observable<NearbySearchResponse>

    @GET("$PATH_SEARCH?rankby=distance")
    fun nearbySearchByDistance(@Query("location") location: String,
                               @Query("keyword") keyword: String,
                               @QueryMap optional: Map<String, String>?)
            : Observable<NearbySearchResponse>

    //endregion

    //region Place Autocomplete

    @GET(PATH_AUTO_COMPLETE)
    fun autoComplete(@Query("input") input: String,
                     @Query("location") location: String,
                     @Query("radius") radius: Int,
                     @QueryMap optional: Map<String, String>?)
            : Observable<AutoCompleteResponse>

    //endregion

    //region Place Query Autocomplete

    @GET(PATH_QUERY_COMPLETE)
    fun queryAutoComplete(@Query("input") input: String,
                          @Query("location") location: String,
                          @Query("radius") radius: Int,
                          @QueryMap optional: Map<String, String>?)
            : Observable<AutoCompleteResponse>

    //endregion

    //region PlaceDetail

    @GET(PATH_DETAIL)
    fun getDetailByPlaceId(@Query("place_id") placeId: String,
                           @QueryMap optional: Map<String, String>?)
            : Observable<PlaceDetailResponse>

//    @GET(PATH_DETAIL)
//    fun getDetailByReference(@Query("reference") reference: String,
//                             @QueryMap optional: Map<String, String>?)
//            : Observable<PlaceDetailResponse>

    //endregion

    companion object {
        private const val PATH_SEARCH = "maps/api/place/nearbysearch/json"

        private const val PATH_AUTO_COMPLETE = "maps/api/place/autocomplete/json"

        private const val PATH_QUERY_COMPLETE = "maps/api/place/queryautocomplete/json"

        private const val PATH_DETAIL = "maps/api/place/details/json"
    }
}