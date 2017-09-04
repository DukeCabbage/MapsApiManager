package com.cabbage.mylibrary

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).

 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ConfigUnitTest {
//    @Test
//    @Throws(Exception::class)
//    fun testDefaultConfig() {
//        val default = Config()
//        default.print()
//        assertEquals(DefaultConfig.endPoint, default.endPoint)
//        assertEquals(DefaultConfig.apiKey, default.apiKey)
//        assertEquals(DefaultConfig.clientId, default.clientId)
//        assertEquals(DefaultConfig.cryptoKey, default.cryptoKey)
//        assertEquals(DefaultConfig.logLevel, default.logLevel)
//        assertEquals(DefaultConfig.authMethod, default.authMethod)
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun testNoAuthMethod() {
//        val config1 = Config(authMethod = AuthMethod.NONE)
//        assert(config1.validate())
//
//        val config2 = Config(authMethod = AuthMethod.NONE, apiKey = "w/e", clientId = "w/e")
//        assert(config2.validate())
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun testApiAuthMethod() {
//        val config1 = Config(authMethod = AuthMethod.API_KEY)
//        assertFalse(config1.validate())
//
//        val config2 = Config(authMethod = AuthMethod.API_KEY, apiKey = "w/e")
//        assert(config2.validate())
//
//        val config3 = Config(authMethod = AuthMethod.API_KEY, apiKey = "w/e", cryptoKey = "w/e")
//        assert(config3.validate())
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun testClientIdAuthMethod() {
//        val config1 = Config(authMethod = AuthMethod.CLIENT_ID)
//        assertFalse(config1.validate())
//
//        val config2 = Config(authMethod = AuthMethod.CLIENT_ID, clientId = "w/e")
//        assertFalse(config2.validate())
//
//        val config3 = Config(authMethod = AuthMethod.CLIENT_ID, cryptoKey = "w/e")
//        assertFalse(config3.validate())
//
//        val config4 = Config(authMethod = AuthMethod.CLIENT_ID, clientId = "w/e", cryptoKey = "w/e")
//        assert(config4.validate())
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun testEndPoint() {
//        val config1 = Config()
//        assert(config1.validate())
//
//        val config2 = Config(endPoint = "")
//        assertFalse(config2.validate())
//
//        val config3 = Config(endPoint = "w/e")
//        assertFalse(config3.validate())
//
//        val config4 = Config(endPoint = "https://test.com/")
//        assert(config4.validate())
//    }
}