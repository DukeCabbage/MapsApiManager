package com.cabbage.mylibrary.place

import android.util.Log
import com.cabbage.mylibrary.authorization.ApiKeyAuthInterceptor
import com.cabbage.mylibrary.authorization.ClientIdAuthInterceptor
import com.cabbage.mylibrary.manager.AuthMethod
import com.cabbage.mylibrary.manager.MapsApiManager
import com.cabbage.mylibrary.manager.handleError
import com.cabbage.mylibrary.place.model.AutoCompleteResponse
import io.reactivex.Observable
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.QueryMap

class PlaceServiceImpl
internal constructor(private var authMethod: AuthMethod = AuthMethod.None(),
                     private var logLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.NONE,
                     internal var nonDefault: Boolean = false) : PlaceService {

    private val TAG: String get() = PlaceServiceImpl::class.java.simpleName

    private var placeApi: PlaceApi? = null

    init {
        buildApi()
    }

    override fun configure(authMethod: AuthMethod?, logLevel: HttpLoggingInterceptor.Level?) {
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

        placeApi = retrofit.create(PlaceApi::class.java)
    }

    override fun autoComplete(input: String,
                              latitude: Double,
                              longitude: Double,
                              radius: Int,
                              language: String)
            : Observable<AutoCompleteResponse> {


        if (latitude !in -90..90) return Observable.error(IllegalArgumentException("Invalid latitude $latitude"))
        if (longitude !in -180..180) return Observable.error(IllegalArgumentException("Invalid longitude $longitude"))
        if (radius <= 0) return Observable.error(IllegalArgumentException("Invalid radius $radius"))

        val param = mutableMapOf(Pair("language", language))

        return autoCompleteInternal(input, "$latitude,$longitude", radius, param)
                .handleError()
    }

    private fun autoCompleteInternal(input: String,
                                     location: String,
                                     radius: Int,
                                     @QueryMap(encoded = false) optional: Map<String, String>?)
            : Observable<AutoCompleteResponse> {

        if (placeApi == null) return Observable.error(IllegalStateException("Place api not instantiated yet"))
        if (input.isBlank()) return Observable.error(IllegalArgumentException("Search input can not be blank"))

        Log.i(TAG, "Auto complete: $input")
        return placeApi!!.autoComplete(input, location, radius, optional)
    }
}