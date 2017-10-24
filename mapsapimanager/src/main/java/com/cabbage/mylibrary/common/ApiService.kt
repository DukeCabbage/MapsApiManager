package com.cabbage.mylibrary.common

import com.cabbage.mylibrary.manager.AuthMethod
import com.cabbage.mylibrary.manager.LogLevel

/**
 * Created by Leo on 2017-09-01.
 * Base service interface
 */
interface ApiService {

    fun configure(authMethod: AuthMethod? = null, logLevel: LogLevel? = null)

    fun resetToGlobalSetting()

//    fun isNonDefault(): Boolean
}