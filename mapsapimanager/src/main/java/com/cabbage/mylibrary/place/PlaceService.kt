package com.cabbage.mylibrary.place

import android.util.Log
import com.cabbage.mylibrary.common.ApiService
import com.cabbage.mylibrary.manager.AuthMethod
import com.cabbage.mylibrary.manager.LogLevel
import com.cabbage.mylibrary.manager.MapsApiManager
import io.reactivex.Observable

interface PlaceService : ApiService {

    fun nearbySearch(
            latitude: Double,
            longitude: Double,
            radius: Int,
            input: String? = null,
            type: String? = null
    ): Observable<NearbySearchResponse>

    fun nearbySearchByDistance(
            latitude: Double,
            longitude: Double,
            input: String,
            type: String? = null
    ): Observable<NearbySearchResponse>

    fun autoComplete(
            input: String,
            latitude: Double,
            longitude: Double,
            radius: Int,
            types: List<String>? = null
    ): Observable<AutoCompleteResponse>

    fun queryComplete(
            input: String,
            latitude: Double,
            longitude: Double,
            radius: Int
    ): Observable<AutoCompleteResponse>

    /*
     * No location bias to cover the whole world
     * https://developers.google.com/places/web-service/autocomplete#location_biasing
     */
    fun autoComplete(input: String): Observable<AutoCompleteResponse>
            = autoComplete(input, 0.0, 0.0, 20000000)

    fun getDetail(placeId: String): Observable<PlaceDetailResponse>

    companion object {
        private val TAG: String get() = PlaceService::class.java.simpleName

        internal fun create(authMethod: AuthMethod? = null, logLevel: LogLevel? = null): PlaceService {
            Log.e(TAG, "PlaceService create: $authMethod, $logLevel")

            val a: AuthMethod = authMethod ?: MapsApiManager.globalAuthMethod
            val b: LogLevel = logLevel ?: MapsApiManager.globalLogLevel

            return PlaceServiceImpl(a, b, nonDefault = false)
        }
    }
}