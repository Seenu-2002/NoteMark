package com.seenu.dev.android.notemark.data.auth_repository

import com.seenu.dev.android.notemark.data.session.token.TokenResponse
import com.seenu.dev.android.notemark.data.User

interface AuthRepository {

    suspend fun register(user: User, password: String)

    suspend fun login(email: String, password: String): TokenResponse

    suspend fun refreshToken(token: String): TokenResponse

}