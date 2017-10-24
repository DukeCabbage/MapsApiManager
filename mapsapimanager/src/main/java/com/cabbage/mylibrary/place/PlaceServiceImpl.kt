package com.cabbage.mylibrary.place

import android.util.Log
import com.cabbage.mylibrary.authorization.ApiKeyAuthInterceptor
import com.cabbage.mylibrary.authorization.ClientIdAuthInterceptor
import com.cabbage.mylibrary.manager.*
import io.reactivex.Observable
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

internal class PlaceServiceImpl
internal constructor(
        private var authMethod: AuthMethod = AuthMethod.None(),
        private var logLevel: LogLevel = LogLevel.None,
        internal var nonDefault: Boolean = false
) : PlaceService {

    private val TAG: String get() = PlaceServiceImpl::class.java.simpleName

    private var placeApi: PlaceApi? = null

    init {
        buildApi()
    }

    override fun configure(authMethod: AuthMethod?, logLevel: LogLevel?) {
        this.authMethod = authMethod ?: this.authMethod
        this.logLevel = logLevel ?: this.logLevel
        //        if (this.authMethod !== MapsApiManager.globalAuthMethod
        //                || this.logLevel !== MapsApiManager.globalLogLevel) {
        //            nonDefault = true
        //        }

        buildApi()
    }

    override fun resetToGlobalSetting() {
        configure(MapsApiManager.globalAuthMethod, MapsApiManager.globalLogLevel)
        nonDefault = false
    }

    //    override fun isNonDefault(): Boolean = nonDefault

    private fun buildApi() {

        val authInterceptor = authMethod.let { method ->
            when (method) {
                is AuthMethod.ApiKey -> ApiKeyAuthInterceptor(method)
                is AuthMethod.ClientId -> ClientIdAuthInterceptor(method)
                else -> Interceptor { chain -> chain.proceed(chain.request()) }
            }
        }

        val loggingInterceptor = HttpLoggingInterceptor().setLevel(
                when (logLevel) {
                    LogLevel.Body -> HttpLoggingInterceptor.Level.BODY
                    LogLevel.Basic -> HttpLoggingInterceptor.Level.BASIC
                    LogLevel.Header -> HttpLoggingInterceptor.Level.HEADERS
                    else -> HttpLoggingInterceptor.Level.NONE
                }
        )

        val client = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .build()

        val retrofit = Retrofit.Builder()
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://maps.googleapis.com/")
                .build()

        placeApi = retrofit.create(PlaceApi::class.java)
    }

    override fun nearbySearch(
            latitude: Double,
            longitude: Double,
            radius: Int,
            input: String?,
            type: String?
    ): Observable<NearbySearchResponse> {

        if (latitude !in -90..90) return Observable.error(IllegalArgumentException("Invalid latitude $latitude"))
        if (longitude !in -180..180) return Observable.error(IllegalArgumentException("Invalid longitude $longitude"))
        if (radius <= 0) return Observable.error(IllegalArgumentException("Invalid radius $radius"))

        val param = mutableMapOf("language" to MapsApiManager.language)
        if (input != null) param.put("keyword", input)
        if (type != null) {
            if (type.contains("|")) Log.w(TAG, "Only one type may be specified (if more than one type is provided, all types following the first entry are ignored)")
            param.put("type", type)
        }

        return nearbySearchInternal("$latitude,$longitude", radius, param)
                .handleError().handleStatusCode()
    }

    override fun nearbySearchByDistance(
            latitude: Double,
            longitude: Double,
            input: String,
            type: String?
    ): Observable<NearbySearchResponse> {
        if (latitude !in -90..90) return Observable.error(IllegalArgumentException("Invalid latitude $latitude"))
        if (longitude !in -180..180) return Observable.error(IllegalArgumentException("Invalid longitude $longitude"))
        if (input.isBlank()) return Observable.error(IllegalArgumentException("Search input can not be blank"))

        val param = mutableMapOf("language" to MapsApiManager.language)
        if (type != null) {
            if (type.contains("|")) Log.w(TAG, "Only one type may be specified (if more than one type is provided, all types following the first entry are ignored)")
            param.put("type", type)
        }

        return nearbySearchInternal("$latitude,$longitude", input, param)
    }

    override fun autoComplete(
            input: String,
            latitude: Double,
            longitude: Double,
            radius: Int,
            types: List<String>?
    ): Observable<AutoCompleteResponse> {

        if (input.isBlank()) return Observable.error(IllegalArgumentException("Search input can not be blank"))
        if (latitude !in -90..90) return Observable.error(IllegalArgumentException("Invalid latitude $latitude"))
        if (longitude !in -180..180) return Observable.error(IllegalArgumentException("Invalid longitude $longitude"))
        if (radius <= 0) return Observable.error(IllegalArgumentException("Invalid radius $radius"))

        val param = mutableMapOf("language" to MapsApiManager.language)
        if (types != null && types.isNotEmpty()) {
            param.put("types", types.joinToString("|"))
        }

        return autoCompleteInternal(input, "$latitude,$longitude", radius, param)
                .handleError().handleStatusCode()
    }

    override fun queryComplete(
            input: String,
            latitude: Double,
            longitude: Double,
            radius: Int
    ): Observable<AutoCompleteResponse> {

        if (input.isBlank()) return Observable.error(IllegalArgumentException("Search input can not be blank"))
        if (latitude !in -90..90) return Observable.error(IllegalArgumentException("Invalid latitude $latitude"))
        if (longitude !in -180..180) return Observable.error(IllegalArgumentException("Invalid longitude $longitude"))
        if (radius <= 0) return Observable.error(IllegalArgumentException("Invalid radius $radius"))

        val param = mutableMapOf("language" to MapsApiManager.language)
        return queryCompleteInternal(input, "$latitude,$longitude", radius, param)
                .handleError().handleStatusCode()
    }

    override fun getDetail(placeId: String)
            : Observable<PlaceDetailResponse> {

        if (placeId.isBlank()) return Observable.error(IllegalArgumentException("Place id can not be blank"))

        val param = mutableMapOf("language" to MapsApiManager.language)

        return getDetailInternal(placeId, param)
                .handleError().handleStatusCode()
    }

    //region Internal methods
    private fun nearbySearchInternal(
            location: String,
            radius: Int,
            optional: Map<String, String>?
    ): Observable<NearbySearchResponse> {

        if (placeApi == null) return Observable.error(IllegalStateException("Place api not instantiated yet"))

        Log.i(TAG, "Nearby search: ${optional?.get("keyword")},\n" +
                "around $location with radius of $radius meters")
        return placeApi!!.nearbySearch(location, radius, optional)
    }

    private fun nearbySearchInternal(
            location: String,
            input: String,
            optional: Map<String, String>?
    ): Observable<NearbySearchResponse> {

        if (placeApi == null) return Observable.error(IllegalStateException("Place api not instantiated yet"))

        Log.i(TAG, "Nearby search : $input,\n" +
                "around $location")
        return placeApi!!.nearbySearchByDistance(location, input, optional)
    }

    private fun autoCompleteInternal(
            input: String,
            location: String,
            radius: Int,
            optional: Map<String, String>?
    ): Observable<AutoCompleteResponse> {

        if (placeApi == null) return Observable.error(IllegalStateException("Place api not instantiated yet"))

        Log.i(TAG, "Auto complete: $input")
        return placeApi!!.autoComplete(input, location, radius, optional)
    }

    private fun queryCompleteInternal(
            input: String,
            location: String,
            radius: Int,
            optional: Map<String, String>?
    ): Observable<AutoCompleteResponse> {

        if (placeApi == null) return Observable.error(IllegalStateException("Place api not instantiated yet"))

        Log.i(TAG, "Auto complete: $input")
        return placeApi!!.queryAutoComplete(input, location, radius, optional)
    }

    private fun getDetailInternal(placeId: String, optional: Map<String, String>?)
            : Observable<PlaceDetailResponse> {

        if (placeApi == null) return Observable.error(IllegalStateException("Place api not instantiated yet"))

        Log.i(TAG, "Get detail by $placeId")
        return placeApi!!.getDetailByPlaceId(placeId, optional)
    }
    //endregion
}