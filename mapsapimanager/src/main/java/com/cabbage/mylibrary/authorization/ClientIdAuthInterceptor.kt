package com.cabbage.mylibrary.authorization

import android.util.Log
import com.cabbage.mylibrary.manager.AuthMethod
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Response

/**
 * Created by Leo on 2017-08-01.
 *
 * This interceptor will alter the request url, and append authentication information onto the url.
 * Use in conjunction with Retrofit, add onto the OkHttpClient used by the Retrofit object
 *
 * This interceptor will append the client id provided by M4B account, then generate an encrypted digital signature
 * (also includes a channel parameter for tracking)
 * See Google Maps API doc: https://developers.google.com/maps/documentation/geocoding/get-api-key#premium-auth
 **/
internal class ClientIdAuthInterceptor(private val clientId: String,
                                       private val cryptoKey: String,
                                       private val channel: String?)
    : Interceptor {

    constructor(a: AuthMethod.ClientId): this(a.clientId, a.cryptoKey, a.channel)

    private val TAG: String get() = ClientIdAuthInterceptor::class.java.simpleName

    override fun intercept(chain: Chain): Response {
        var urlString = chain.request().url().toString()
        Log.v(TAG, "Original url: $urlString")
        Log.v(TAG, "Append client id and channel: $clientId, $channel")

        urlString += "&client=$clientId"
        channel?.let { urlString += "&channel=$it" }

        try {
            // Sign with crypto key and append signature
            urlString += UrlSigner(cryptoKey).signUrl(urlString)
        } catch (e: Exception) {
            Log.w(TAG, e)
        }

        val request = chain.request().newBuilder().url(urlString).build()
        Log.v(TAG, "Signed url: ${request.url()}")
        return chain.proceed(request)
    }
}