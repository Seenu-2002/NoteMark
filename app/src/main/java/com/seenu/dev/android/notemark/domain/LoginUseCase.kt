package com.seenu.dev.android.notemark.domain

import com.seenu.dev.android.notemark.data.User
import com.seenu.dev.android.notemark.data.auth_repository.AuthRepository
import com.seenu.dev.android.notemark.domain.session.SessionManager

class LoginUseCase constructor(
    val repository: AuthRepository,
    private val sessionManager: SessionManager
) {

    suspend fun login(username: String, password: String): User {
        val token = repository.login(username, password)
        if (token.accessToken.isNotEmpty()) {
            sessionManager.storeAccessToken(
                token.accessToken,
                System.currentTimeMillis() + (15 * 60 * 1000) // 15 minutes
            )
            sessionManager.storeRefreshToken(
                token.refreshToken,
                System.currentTimeMillis() + (30 * 24 * 60 * 60 * 1000) // 30 days
            )
            return User(
                userName = username,
                email = ""
            )
        } else {
            throw Exception("Login failed, no access token received")
        }
    }

}