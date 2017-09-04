package com.cabbage.mylibrary.geocoding

import android.util.Log
import com.cabbage.mylibrary.ApiService
import com.cabbage.mylibrary.geocoding.model.GeocodingResponse
import com.cabbage.mylibrary.manager.AuthMethod
import com.cabbage.mylibrary.manager.ReqBounds
import com.cabbage.mylibrary.manager.MapsApiManager
import io.reactivex.Observable
import okhttp3.logging.HttpLoggingInterceptor.Level

interface GeocodingService: ApiService {

    fun queryByAddressName(name: String,
                           bounds: ReqBounds? = null,
                           language: String? = "en",
                           region: String? = null,
                           components: Map<String, String>? = null)
            : Observable<GeocodingResponse>

    /**
     * Reverse geocoding, translating a location on the map into a human-readable address
     */
    fun queryByLocation(latitude: Double,
                        longitude: Double,
                        language: String? = "en",
                        resultType: String? = null,
                        locationType: String? = null)
            : Observable<GeocodingResponse>

    /**
     * Reverse geocoding, with place id
     * {@see https://developers.google.com/maps/documentation/geocoding/intro#place-id}
     */
    fun queryByPlaceId(placeId: String,
                       language: String? = "en",
                       resultType: String? = null,
                       locationType: String? = null)
            : Observable<GeocodingResponse>

    companion object Factory {
        private val TAG: String get() = GeocodingService::class.java.simpleName

        // Let's not expose this for now
        internal fun create(authMethod: AuthMethod? = null, logLevel: Level? = null): GeocodingServiceImpl {
            Log.i(TAG, "GeocodingService create: $authMethod, $logLevel")

            val a: AuthMethod = authMethod ?: MapsApiManager.globalAuthMethod
            val b: Level = logLevel ?: MapsApiManager.globalLogLevel

            return GeocodingServiceImpl(a, b, nonDefault = false)
        }
    }
}