package com.cabbage.mylibrary.geocoding

import com.cabbage.mylibrary.geocoding.model.GeocodingResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface GeocodingApi {

    @GET(PATH)
    fun getAddressFromLocation(@Query("latlng") latLng: String,
                               @QueryMap(encoded = false) optional: Map<String, String>?)
            : Observable<GeocodingResponse>

    @GET(PATH)
    fun getAddressFromPlaceId(@Query("place_id") placeId: String,
                              @QueryMap(encoded = false) optional: Map<String, String>?)
            : Observable<GeocodingResponse>

    @GET(PATH)
    fun getAddressFromName(@Query("address") address: String,
                           @QueryMap(encoded = false) optional: Map<String, String>? = null)
            : Observable<GeocodingResponse>

    companion object {
        private const val PATH = "maps/api/geocode/json"
    }
}