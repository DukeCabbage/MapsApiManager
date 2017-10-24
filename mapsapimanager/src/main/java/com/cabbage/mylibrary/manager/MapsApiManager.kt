package com.cabbage.mylibrary.manager

import com.cabbage.mylibrary.geocoding.GeocodingService
import com.cabbage.mylibrary.place.PlaceService

object MapsApiManager {

    private var initialized: Boolean = false

    var language: String = "en"
        private set

    fun configureLanguage(s: String?) {
        language = s ?: "en"
    }

    //    var globalLogLevel: LogLevel = LogLevel.None
    //        set(value) {
    //            field = value
    //            geocodingService.let { if (it.isNonDefault()) it.configure(logLevel = value) }
    //            placeService.let { if (it.isNonDefault()) it.configure(logLevel = value) }
    //        }
    //
    //    var globalAuthMethod: AuthMethod = AuthMethod.None()
    //        set(value) {
    //            field = value
    //            geocodingService.let { if (it.isNonDefault()) it.configure(authMethod = value) }
    //            placeService.let { if (it.isNonDefault()) it.configure(authMethod = value) }
    //        }

    internal lateinit var globalLogLevel: LogLevel

    internal lateinit var globalAuthMethod: AuthMethod

    lateinit var geocodingService: GeocodingService

    lateinit var placeService: PlaceService

    fun initialize(authMethod: AuthMethod = AuthMethod.None(),
                   logLevel: LogLevel = LogLevel.None) {

        globalLogLevel = logLevel
        globalAuthMethod = authMethod

        geocodingService = GeocodingService.create()
        placeService = PlaceService.create()
        initialized = true
    }
}