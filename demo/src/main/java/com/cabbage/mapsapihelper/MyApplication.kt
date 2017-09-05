package com.cabbage.mapsapihelper

import android.app.Application
import com.cabbage.mylibrary.manager.AuthMethod
import com.cabbage.mylibrary.manager.MapsApiManager
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

class MyApplication : Application() {

    private val mDebugTree = object : Timber.DebugTree() {
        override fun createStackElementTag(element: StackTraceElement): String =
                super.createStackElementTag(element) + ":" + element.lineNumber
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(mDebugTree)

        MapsApiManager.globalLogLevel = HttpLoggingInterceptor.Level.BODY
        MapsApiManager.placeService.configure(authMethod = AuthMethod.ApiKey("AIzaSyC-QCjP9MPW7j_Lu22NeIIlBDs5VJx5vxU"))

    }
}