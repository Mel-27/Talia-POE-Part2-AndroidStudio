package com.gia.poe_demo

import java.security.MessageDigest

// utility object for hashing passwords using MD5
// ref: https://developer.android.com/reference/java/security/MessageDigest
object HashUtils {

    // hashes a plain text password using MD5 and returns the hex string
    fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}