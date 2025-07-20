package com.seenu.dev.android.notemark.domain.session

interface SessionManager {

    suspend fun isUserLoggedIn(): Boolean

    suspend fun getAccessToken(): String?

    suspend fun storeAccessToken(token: String, validUntil: Long)

    suspend fun storeRefreshToken(token: String, validUntil: Long)

    suspend fun clearSession()

}