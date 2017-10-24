package com.cabbage.mylibrary.geocoding

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface GeocodingApi {

    @GET(PATH)
    fun getAddressFromLocation(@Query("latlng") latLng: String,
                               @QueryMap optional: Map<String, String>?)
            : Observable<GeocodingResponse>

    @GET(PATH)
    fun getAddressFromPlaceId(@Query("place_id") placeId: String,
                              @QueryMap optional: Map<String, String>?)
            : Observable<GeocodingResponse>

    @GET(PATH)
    fun getAddressFromName(@Query("address") address: String,
                           @QueryMap optional: Map<String, String>?)
            : Observable<GeocodingResponse>

    companion object {
        private const val PATH = "maps/api/geocode/json"
    }
}