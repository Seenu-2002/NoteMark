package com.seenu.dev.android.notemark.utils

import android.content.Context
import android.util.Base64
import androidx.core.content.edit

class EncryptedSharedPreference(
    private val prefName: String,
    private val context: Context,
    private val securityUtil: SecurityUtil,
    private val mode: Int = Context.MODE_PRIVATE
) {

    private val pref by lazy { context.getSharedPreferences(prefName, mode) }

    fun putString(key: String, value: String) {
        val (iv, encrypted) = securityUtil.encryptData(value)

        val ivBase64 = Base64.encodeToString(iv, Base64.NO_WRAP)
        val encryptedBase64 = Base64.encodeToString(encrypted, Base64.NO_WRAP)
        val combined = "$ivBase64:$encryptedBase64"

        pref.edit { putString(key, combined) }
    }

    fun getString(key: String, defaultValue: String? = null): String? {
        val combined = pref.getString(key, null) ?: return defaultValue

        val parts = combined.split(":")
        if (parts.size != 2) return defaultValue

        val iv = Base64.decode(parts[0], Base64.NO_WRAP)
        val encrypted = Base64.decode(parts[1], Base64.NO_WRAP)

        return securityUtil.decryptData(iv, encrypted)
    }

    fun putLong(key: String, value: Long) {
        putString(key, value.toString())
    }

    fun getLong(key: String, defaultValue: Long = 0L): Long {
        val value = getString(key) ?: return defaultValue
        return value.toLongOrNull() ?: defaultValue
    }

    fun putInt(key: String, value: Int) {
        putString(key, value.toString())
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        val value = getString(key) ?: return defaultValue
        return value.toIntOrNull() ?: defaultValue
    }

    fun putBoolean(key: String, value: Boolean) {
        putString(key, value.toString())
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        val value = getString(key) ?: return defaultValue
        return value.toBooleanStrictOrNull() ?: defaultValue
    }

    fun clear() {
        pref.edit { clear() }
    }
}