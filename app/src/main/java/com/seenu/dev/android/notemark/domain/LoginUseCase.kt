package com.seenu.dev.android.notemark.domain

import com.seenu.dev.android.notemark.data.User
import com.seenu.dev.android.notemark.data.auth_repository.AuthRepository
import com.seenu.dev.android.notemark.domain.session.SessionManager
import timber.log.Timber

class LoginUseCase constructor(
    val repository: AuthRepository,
    private val sessionManager: SessionManager
) {

    suspend fun login(email: String, password: String): User {
        val tokenResponse = repository.login(email, password)
        if (tokenResponse.accessToken.isNotEmpty()) {
            sessionManager.storeAccessToken(
                tokenResponse.accessToken,
                System.currentTimeMillis() + (15L * 60 * 1000) // 15 minutes
            )
            sessionManager.storeRefreshToken(
                tokenResponse.refreshToken,
                System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000) // 30 days
            )
            val user = User(
                userName = tokenResponse.userName,
                email = email
            )
            sessionManager.storeUser(user)
            return user
        } else {
            // FIXME: Proper error handling
            Timber.e("Login failed, no access token received")
            throw Exception("Login failed, no access token received")
        }
    }

}