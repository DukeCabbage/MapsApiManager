package com.cabbage.mylibrary.geocoding

import android.util.Log
import com.cabbage.mylibrary.common.ApiService
import com.cabbage.mylibrary.manager.AuthMethod
import com.cabbage.mylibrary.manager.LogLevel
import com.cabbage.mylibrary.manager.MapsApiManager
import com.cabbage.mylibrary.manager.ReqBounds
import io.reactivex.Observable

interface GeocodingService : ApiService {

    fun queryByAddressName(
            name: String,
            bounds: ReqBounds? = null,
            region: String? = null,
            components: Map<String, String>? = null
    ): Observable<GeocodingResponse>

    /**
     * Reverse geocoding, translating a location on the map into a human-readable address
     */
    fun queryByLocation(
            latitude: Double,
            longitude: Double,
            resultType: List<String>? = null,
            locationType: List<String>? = null
    ): Observable<GeocodingResponse>

    /**
     * Reverse geocoding, with place id
     * {@see https://developers.google.com/maps/documentation/geocoding/intro#place-id}
     */
    fun queryByPlaceId(placeId: String): Observable<GeocodingResponse>

    companion object Factory {
        private val TAG: String get() = GeocodingService::class.java.simpleName

        // Let's not expose this for now
        internal fun create(authMethod: AuthMethod? = null, logLevel: LogLevel? = null): GeocodingServiceImpl {
            Log.i(TAG, "GeocodingService create: $authMethod, $logLevel")

            val a: AuthMethod = authMethod ?: MapsApiManager.globalAuthMethod
            val b: LogLevel = logLevel ?: MapsApiManager.globalLogLevel

            return GeocodingServiceImpl(a, b, nonDefault = false)
        }
    }
}