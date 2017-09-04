package com.cabbage.mylibrary.geocoding

import android.util.Log
import com.cabbage.mylibrary.authorization.ApiKeyAuthInterceptor
import com.cabbage.mylibrary.authorization.ClientIdAuthInterceptor
import com.cabbage.mylibrary.geocoding.model.GeocodingResponse
import com.cabbage.mylibrary.manager.*
import io.reactivex.Observable
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Leo on 2017-08-29.
 *
 */
class GeocodingServiceImpl
internal constructor(private var authMethod: AuthMethod = AuthMethod.None(),
                     private var logLevel: Level = Level.NONE,
                     internal var nonDefault: Boolean = false) : GeocodingService {

    private val TAG: String get() = GeocodingServiceImpl::class.java.simpleName

    private var geocodingApi: GeocodingApi? = null

    init {
        buildApi()
    }

    override fun configure(authMethod: AuthMethod?, logLevel: Level?) {
        this.authMethod = authMethod ?: this.authMethod
        this.logLevel = logLevel ?: this.logLevel
        if (this.authMethod !== MapsApiManager.globalAuthMethod
                || this.logLevel !== MapsApiManager.globalLogLevel) {
            nonDefault = true
        }

        buildApi()
    }

    override fun resetToGlobalSetting() {
        configure(MapsApiManager.globalAuthMethod, MapsApiManager.globalLogLevel)
        nonDefault = false
    }

    override fun isNonDefault(): Boolean = nonDefault

    private fun buildApi() {

        val authInterceptor = authMethod.let { method ->
            when (method) {
                is AuthMethod.ApiKey -> ApiKeyAuthInterceptor(method)
                is AuthMethod.ClientId -> ClientIdAuthInterceptor(method)
                else -> Interceptor { chain -> chain.proceed(chain.request()) }
            }
        }
        val loggingInterceptor = HttpLoggingInterceptor().setLevel(logLevel)

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

        geocodingApi = retrofit.create(GeocodingApi::class.java)
    }

    override fun queryByAddressName(name: String,
                                    bounds: ReqBounds?,
                                    language: String?,
                                    region: String?,
                                    components: Map<String, String>?): Observable<GeocodingResponse> {

        val param = mutableMapOf<String, String>()
        if (language != null) {
            param.put("language", language)
        }
        if (bounds != null) {
            val strBounds = "${bounds.southWest.lat},${bounds.southWest.lng}|${bounds.northEast.lat},${bounds.northEast.lng}"
            param.put("bounds", strBounds)
        }

        // https://en.wikipedia.org/wiki/List_of_Internet_top-level_domains#Country_code_top-level_domains
        if (!region.isNullOrBlank()) param.put("region", region!!)
        if (components != null && components.isNotEmpty()) {
            val strComponents = components.map { entry -> "${entry.key}:${entry.value}" }
                    .reduce { acc, s -> "$acc|$s" }
                    .dropLastWhile { it == '|' }
            param.put("components", strComponents)
        }

        return getAddressFromNameInternal(name, param)
    }

    override fun queryByLocation(latitude: Double,
                                 longitude: Double,
                                 language: String?,
                                 resultType: String?,
                                 locationType: String?): Observable<GeocodingResponse> {

        val param = mutableMapOf<String, String>()
        if (language != null) {
            param.put("language", language)
        }
        if (!resultType.isNullOrBlank()) param.put("result_type", resultType!!)
        if (!locationType.isNullOrBlank()) param.put("location_type", locationType!!)

        return getAddressFromLocationInternal(latitude, longitude, param)
    }

    override fun queryByPlaceId(placeId: String,
                                language: String?,
                                resultType: String?,
                                locationType: String?): Observable<GeocodingResponse> {

        val param = mutableMapOf<String, String>()
        if (language != null) {
            param.put("language", language)
        }
        if (!resultType.isNullOrBlank()) param.put("result_type", resultType!!)
        if (!locationType.isNullOrBlank()) param.put("location_type", locationType!!)

        return getAddressFromPlaceIdInternal(placeId, param)
    }

    private fun getAddressFromNameInternal(name: String,
                                           optional: Map<String, String>? = null)
            : Observable<GeocodingResponse> {

        if (geocodingApi == null) return Observable.error(IllegalStateException("Geo-coding api not instantiated yet"))
        if (name.isBlank()) return Observable.error(IllegalArgumentException("Address name can not be blank"))

        name.replace(" ", "+")

        Log.i(TAG, "Get address with $name")
        return geocodingApi!!.getAddressFromName(name, optional)
                .handleError().handleStatusCode()
    }

    private fun getAddressFromLocationInternal(latitude: Double,
                                               longitude: Double,
                                               optional: Map<String, String>? = null)
            : Observable<GeocodingResponse> {

        if (geocodingApi == null) return Observable.error(IllegalStateException("Geo-coding api not instantiated yet"))
        if (latitude !in -90..90) return Observable.error(IllegalArgumentException("Invalid latitude $latitude"))
        if (longitude !in -180..180) return Observable.error(IllegalArgumentException("Invalid longitude $longitude"))

        Log.i(TAG, "Get address with $latitude,$longitude")
        return geocodingApi!!.getAddressFromLocation("$latitude,$longitude", optional)
                .handleError().handleStatusCode()

    }

    private fun getAddressFromPlaceIdInternal(placeId: String,
                                              optional: Map<String, String>? = null)
            : Observable<GeocodingResponse> {

        if (geocodingApi == null) return Observable.error(IllegalStateException("Geo-coding api not instantiated yet"))
        if (placeId.isBlank()) return Observable.error(IllegalArgumentException("Place id can not be blank"))

        Log.i(TAG, "Get address with $placeId")
        return geocodingApi!!.getAddressFromPlaceId(placeId, optional)
                .handleError().handleStatusCode()
    }

//    private fun Observable<GeocodingResponse>.handleError()
//            : Observable<GeocodingResponse>
//            = onErrorResumeNext { t: Throwable ->
//        if (t is HttpException) {
//            val res = Gson().safeFromJson(t.response().errorBody()?.string(), GeocodingResponse::class.java)
//            if (res != null) return@onErrorResumeNext Observable.error(MapsApiError(res, t))
//        } else if (t is UnknownHostException) {
//            return@onErrorResumeNext Observable.error(MapsApiError("Unable to connect", t))
//        }
//        return@onErrorResumeNext Observable.error(t)
//    }
//
//    /**
//     * wraps around Gson.fromJson(String json, Class<T> classOfT), returns null if error happens
//     */
//    private fun <T> Gson.safeFromJson(json: String?, classOfT: Class<T>): T? {
//        if (json.isNullOrBlank()) return null
//        return try {
//            fromJson(json, classOfT)
//        } catch (e: JsonSyntaxException) {
//            null
//        }
//    }
}