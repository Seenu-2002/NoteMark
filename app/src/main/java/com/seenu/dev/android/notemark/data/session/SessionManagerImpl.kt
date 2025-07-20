package com.seenu.dev.android.notemark.data.session

import android.content.Context
import com.seenu.dev.android.notemark.data.auth_repository.AuthRepository
import com.seenu.dev.android.notemark.domain.session.SessionManager
import com.seenu.dev.android.notemark.utils.EncryptedSharedPreference
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class SessionManagerImpl constructor(
    private val authRepository: AuthRepository
) : SessionManager, KoinComponent {

    private val sharedPreference: EncryptedSharedPreference by inject {
        parametersOf(
            PREF_NAME,
            Context.MODE_PRIVATE
        )
    }

    companion object {
        private const val PREF_NAME = "note_mark_session_pref"
        private const val ACCESS_TOKEN = "access_token"
        private const val REFRESH_TOKEN = "refresh_token"
        private const val ACCESS_TOKEN_VALID_UNTIL = "access_token_valid_until"
        private const val REFRESH_TOKEN_VALID_UNTIL = "refresh_token_valid_until"
    }

    override suspend fun isUserLoggedIn(): Boolean {
        val refreshTokenValidUntil = sharedPreference.getLong(REFRESH_TOKEN_VALID_UNTIL, 0L)
        return refreshTokenValidUntil >= System.currentTimeMillis() &&
                sharedPreference.getString(REFRESH_TOKEN, null) != null &&
                sharedPreference.getString(ACCESS_TOKEN, null) != null
    }

    override suspend fun getAccessToken(): String? {
        val validTill = sharedPreference.getLong(ACCESS_TOKEN_VALID_UNTIL, 0L)
        return if (validTill >= System.currentTimeMillis()) {
            val token = sharedPreference.getString(ACCESS_TOKEN, null)
            token ?: refreshAccessToken()
        } else {
            refreshAccessToken()
        }
    }

    private suspend fun refreshAccessToken(): String? {
        val refreshTokenValidUntil =
            sharedPreference.getLong(REFRESH_TOKEN_VALID_UNTIL, System.currentTimeMillis())
        if (refreshTokenValidUntil < System.currentTimeMillis()) {
            clearSession()
            return null
        } else {
            val refreshToken = sharedPreference.getString(REFRESH_TOKEN, null)
            if (refreshToken != null) {
                val response = authRepository.refreshToken(refreshToken)
                if (response.accessToken.isNotEmpty()) {
                    storeAccessToken(
                        response.accessToken,
                        System.currentTimeMillis() * (15 * 60 * 1000)
                    ) // 15 minutes validity
                    storeRefreshToken(
                        response.refreshToken,
                        System.currentTimeMillis() + (30 * 24 * 60 * 60 * 1000)
                    ) // 30 days validity
                    return response.accessToken
                } else {
                    clearSession()
                    return null
                }
            } else {
                clearSession()
                return null
            }
        }
    }

    override suspend fun storeAccessToken(token: String, validUntil: Long) {
        sharedPreference.putString(ACCESS_TOKEN, token)
        sharedPreference.putLong(ACCESS_TOKEN_VALID_UNTIL, validUntil)
    }

    override suspend fun storeRefreshToken(token: String, validUntil: Long) {
        sharedPreference.putString(REFRESH_TOKEN, token)
        sharedPreference.putLong(REFRESH_TOKEN_VALID_UNTIL, validUntil)
    }

    override suspend fun clearSession() {
        sharedPreference.clear()
    }
}