package com.cabbage.mylibrary.manager

import com.cabbage.mylibrary.GenericRes
import com.cabbage.mylibrary.geocoding.model.GeocodingResponse
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import io.reactivex.Observable
import retrofit2.HttpException
import java.net.UnknownHostException

class ReqLatLng(val lat: Double, val lng: Double)
class ReqBounds(val southWest: ReqLatLng, val northEast: ReqLatLng)

open class MapsApiError : RuntimeException {

    constructor(message: String, cause: Throwable? = null) : super(message, cause)

    constructor(res: GenericRes<*>, cause: Throwable? = null)
            : this("${res.status}, message: ${res.errorMessage}", cause)
}


/**
 * Expects an error body in the lower format, which is in a similar pattern as a successful response
 * {
 *     results : [],
 *     status: xxx,
 *     error_message: xxx
 * }
 */
internal fun <T : GenericRes<*>> Observable<T>.handleError()
        : Observable<T>
        = onErrorResumeNext { t: Throwable ->
    if (t is HttpException) {
        val res = Gson().safeFromJson(t.response().errorBody()?.string(), GeocodingResponse::class.java)
        if (res != null) return@onErrorResumeNext Observable.error(MapsApiError(res, t))
    } else if (t is UnknownHostException) {
        return@onErrorResumeNext Observable.error(MapsApiError("Unable to connect", t))
    }
    return@onErrorResumeNext Observable.error(t)
}

internal fun <T : GenericRes<*>> Observable<T>.handleStatusCode()
        : Observable<T>
        = flatMap { res: T ->
    if (res.status == "OK") {
        Observable.just(res)
    } else {
        return@flatMap Observable.error<T>(MapsApiError(res))
    }
}

/**
 * wraps around Gson.fromJson(String json, Class<T> classOfT), returns null if error happens
 */
private fun <T> Gson.safeFromJson(json: String?, classOfT: Class<T>): T? {
    if (json.isNullOrBlank()) return null
    return try {
        fromJson(json, classOfT)
    } catch (e: JsonSyntaxException) {
        null
    }
}