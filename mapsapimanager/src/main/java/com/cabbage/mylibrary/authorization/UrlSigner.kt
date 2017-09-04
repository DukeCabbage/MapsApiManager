package com.cabbage.mylibrary.authorization

import android.util.Base64
import android.util.Log
import java.io.UnsupportedEncodingException
import java.net.URISyntaxException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

internal class UrlSigner(var cryptoKey: String) {

    private val TAG: String get() = UrlSigner::class.java.simpleName

    private var key: ByteArray

    init {
        // Convert the key from 'web safe' base 64 to binary
        cryptoKey = cryptoKey.replace('-', '+').replace('_', '/')
        Log.v(TAG, "Key: " + cryptoKey)
        // Base64 is JDK 1.8 only - older versions may need to use Apache Commons or similar.
        this.key = Base64.decode(cryptoKey, Base64.DEFAULT)
    }

    /**
     * See https://developers.google.com/maps/documentation/geocoding/get-api-key#digital-signature-premium
     */
    @Throws(NoSuchAlgorithmException::class, InvalidKeyException::class, UnsupportedEncodingException::class, URISyntaxException::class)
    fun signUrl(urlString: String): String {

        val strippedUrlString = urlString.replace("https://maps.googleapis.com/", "/")
        Log.v(TAG, "URL Portion to Sign: $strippedUrlString")

        // Get an HMAC-SHA1 signing key from the raw key bytes
        val sha1Key = SecretKeySpec(key, "HmacSHA1")

        // Get an HMAC-SHA1 Mac INSTANCE and initialize it with the HMAC-SHA1 key
        val mac = Mac.getInstance("HmacSHA1")
        mac.init(sha1Key)

        // compute the binary signature for the request
        val sigBytes = mac.doFinal(strippedUrlString.toByteArray())

        // base 64 encode the binary signature
        // Base64 is JDK 1.8 only - older versions may need to use Apache Commons or similar.
        var signature = Base64.encodeToString(sigBytes, Base64.DEFAULT)
        // convert the signature to 'web safe' base 64
        signature = signature.replace('+', '-')
        signature = signature.replace('/', '_')

        return "&signature=" + signature
    }
}