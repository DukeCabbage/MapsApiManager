package com.cabbage.mylibrary.authorization

import android.util.Log
import com.cabbage.mylibrary.manager.AuthMethod
import okhttp3.Interceptor
import okhttp3.Response

/**
 *  Created by Leo on 2017-08-01.
 *
 * This interceptor will alter the request url, and append authentication information onto the url.
 * Use in conjunction with Retrofit, add onto the OkHttpClient used by the Retrofit object
 *
 * This interceptor will append an api key
 * See Google Maps API doc: https://developers.google.com/maps/documentation/geocoding/get-api-key#premium-auth
 **/
internal class ApiKeyAuthInterceptor(private val apiKey: String)
    : Interceptor {

    constructor(a: AuthMethod.ApiKey) : this(a.key)

    private val TAG: String get() = ApiKeyAuthInterceptor::class.java.simpleName

    override fun intercept(chain: Interceptor.Chain): Response {
        var urlString = chain.request().url().toString()
        Log.v(TAG, "Original url: $urlString")
        Log.v(TAG, "Authenticate with api key: $apiKey")

        urlString += "&key=$apiKey"

        val request = chain.request().newBuilder().url(urlString).build()
        Log.v(TAG, "New url: ${request.url()}")
        return chain.proceed(request)
    }
}