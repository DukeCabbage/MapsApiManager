package com.cabbage.mylibrary.manager

sealed class AuthMethod {
    @Suppress("unused")
    class None(@JvmField val unit: Unit = Unit) : AuthMethod() {
        override fun toString(): String = "None()"
    }

    @Suppress("MemberVisibilityCanPrivate")
    class ApiKey(@JvmField val key: String) : AuthMethod() {
        init {
            if (key.isBlank()) throw IllegalArgumentException("Api key can not be blank")
        }

        override fun toString(): String = "ApiKey(key='$key')"
    }

    @Suppress("MemberVisibilityCanPrivate")
    class ClientId(@JvmField val clientId: String,
                   @JvmField val cryptoKey: String,
                   @JvmField val channel: String? = null) : AuthMethod() {
        init {
            if (clientId.isBlank()) throw IllegalArgumentException("Client id can not be blank")
            if (cryptoKey.isBlank()) throw IllegalArgumentException("Crypto key can not be blank")
        }

        override fun toString(): String = "ClientId(clientId='$clientId', cryptoKey='$cryptoKey', channel=$channel)"
    }
}

