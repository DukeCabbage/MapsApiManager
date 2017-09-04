package com.cabbage.mylibrary

import com.cabbage.mylibrary.manager.AuthMethod
import okhttp3.logging.HttpLoggingInterceptor

/**
 * Created by Leo on 2017-09-01.
 * Base service interface
 */
interface ApiService {

    fun configure(authMethod: AuthMethod? = null, logLevel: HttpLoggingInterceptor.Level? = null)

    fun resetToGlobalSetting()

    fun isNonDefault(): Boolean
}