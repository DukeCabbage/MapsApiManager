package com.cabbage.mylibrary.place

import android.util.Log
import com.cabbage.mylibrary.ApiService
import com.cabbage.mylibrary.manager.AuthMethod
import com.cabbage.mylibrary.manager.MapsApiManager
import com.cabbage.mylibrary.place.model.AutoCompleteResponse
import io.reactivex.Observable
import okhttp3.logging.HttpLoggingInterceptor.Level

interface PlaceService : ApiService {

    fun autoComplete(input: String,
                     latitude: Double,
                     longitude: Double,
                     radius: Int = 20000,
                     language: String = "en")
            : Observable<AutoCompleteResponse>

    /**
     * No location bias to cover the whole world
     * https://developers.google.com/places/web-service/autocomplete#location_biasing
     */
    fun autoComplete(input: String,
                     language: String = "en")
            : Observable<AutoCompleteResponse>
            = autoComplete(input, 0.0, 0.0, 20000000)

    companion object {
        private val TAG: String get() = PlaceService::class.java.simpleName

        internal fun create(authMethod: AuthMethod? = null, logLevel: Level? = null): PlaceService {
            Log.e(TAG, "PlaceService create: $authMethod, $logLevel")

            val a: AuthMethod = authMethod ?: MapsApiManager.globalAuthMethod
            val b: Level = logLevel ?: MapsApiManager.globalLogLevel

            return PlaceServiceImpl(a, b, nonDefault = false)
        }
    }
}