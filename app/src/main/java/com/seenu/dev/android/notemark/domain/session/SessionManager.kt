package com.seenu.dev.android.notemark.domain.session

import com.seenu.dev.android.notemark.data.User

interface SessionManager {

    suspend fun isUserLoggedIn(): Boolean

    suspend fun getAccessToken(): String?

    suspend fun getUser(): User?

    suspend fun storeAccessToken(token: String, validUntil: Long)

    suspend fun storeRefreshToken(token: String, validUntil: Long)

    suspend fun storeUser(user: User)


    suspend fun clearSession()

}