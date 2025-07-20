package com.seenu.dev.android.notemark.utils

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties.BLOCK_MODE_GCM
import android.security.keystore.KeyProperties.ENCRYPTION_PADDING_NONE
import android.security.keystore.KeyProperties.KEY_ALGORITHM_AES
import android.security.keystore.KeyProperties.PURPOSE_DECRYPT
import android.security.keystore.KeyProperties.PURPOSE_ENCRYPT
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class SecurityUtil constructor(
    private val keyAlias: String
) {

    companion object {
        private const val PROVIDER = "AndroidKeyStore"
    }

    private val cipher by lazy {
        Cipher.getInstance("AES/GCM/NoPadding")
    }
    private val charset by lazy {
        Charsets.UTF_8
    }
    private val keyStore by lazy {
        KeyStore.getInstance(PROVIDER).apply {
            load(null)
        }
    }
    private val keyGenerator by lazy {
        KeyGenerator.getInstance(KEY_ALGORITHM_AES, PROVIDER)
    }

    fun encryptData(text: String): Pair<ByteArray, ByteArray> {
        val secretKey = generateSecretKey(keyAlias)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedData = cipher.doFinal(text.toByteArray(charset))
        val iv = cipher.iv
        return Pair(iv, encryptedData)
    }

    fun decryptData(iv: ByteArray, encryptedData: ByteArray): String {
        val secretKey = getSecretKey(keyAlias)
        val gcmParameterSpec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec)
        return cipher.doFinal(encryptedData).toString(charset)
    }

    @Synchronized
    fun generateSecretKey(keyAlias: String): SecretKey {
        val keyEntry = keyStore.getEntry(keyAlias, null)
        return (if (keyEntry == null) {
            keyGenerator.apply {
                init(
                    KeyGenParameterSpec
                        .Builder(keyAlias, PURPOSE_ENCRYPT or PURPOSE_DECRYPT)
                        .setBlockModes(BLOCK_MODE_GCM)
                        .setEncryptionPaddings(ENCRYPTION_PADDING_NONE)
                        .build()
                )
            }.generateKey()
        } else {
            getSecretKey(keyAlias)
        })
    }

    private fun getSecretKey(keyAlias: String): SecretKey {
        return (keyStore.getEntry(keyAlias, null) as KeyStore.SecretKeyEntry).secretKey
    }

}