package com.seenu.dev.android.notemark.data.session

import android.content.Context
import com.seenu.dev.android.notemark.data.User
import com.seenu.dev.android.notemark.data.auth_repository.AuthRepository
import com.seenu.dev.android.notemark.domain.session.SessionManager
import com.seenu.dev.android.notemark.utils.EncryptedSharedPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import timber.log.Timber

class SessionManagerImpl constructor(
    private val authRepository: AuthRepository
) : SessionManager, KoinComponent {
    
    private val dispatcher = Dispatchers.IO

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
        private const val USER_NAME = "user_name"
        private const val USER_EMAIL = "email"
    }

    override suspend fun isUserLoggedIn(): Boolean {
        return withContext(dispatcher) {
            val refreshTokenValidUntil = sharedPreference.getLong(REFRESH_TOKEN_VALID_UNTIL, 0L)
            Timber.d("Refresh token valid until: $refreshTokenValidUntil")
            refreshTokenValidUntil >= System.currentTimeMillis() &&
                    sharedPreference.getString(REFRESH_TOKEN, null) != null &&
                    sharedPreference.getString(ACCESS_TOKEN, null) != null
        }
    }

    override suspend fun getUser(): User? {
        return withContext(dispatcher) {
            val name = sharedPreference.getString(USER_NAME, null)
            val email = sharedPreference.getString(USER_EMAIL, null)
            if (name != null && email != null) {
                User(userName = name, email = email)
            } else {
                null
            }
        }
    }

    override suspend fun getAccessToken(): String? {
        return withContext(dispatcher) {
            val validTill = sharedPreference.getLong(ACCESS_TOKEN_VALID_UNTIL, 0L)
            if (validTill >= System.currentTimeMillis()) {
                val token = sharedPreference.getString(ACCESS_TOKEN, null)
                token ?: refreshAccessToken()
            } else {
                refreshAccessToken()
            }
        }
    }

    private suspend fun refreshAccessToken(): String? {
        return withContext(dispatcher) {
            val refreshTokenValidUntil =
                sharedPreference.getLong(REFRESH_TOKEN_VALID_UNTIL, System.currentTimeMillis())
            if (refreshTokenValidUntil < System.currentTimeMillis()) {
                clearSession()
                null
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
                        response.accessToken
                    } else {
                        clearSession()
                        null
                    }
                } else {
                    clearSession()
                    null
                }
            }
        }
    }

    override suspend fun storeAccessToken(token: String, validUntil: Long) {
        return withContext(dispatcher) {
            sharedPreference.putString(ACCESS_TOKEN, token)
            sharedPreference.putLong(ACCESS_TOKEN_VALID_UNTIL, validUntil)
            Timber.d("Access token stored, valid until $validUntil")
        }
    }

    override suspend fun storeRefreshToken(token: String, validUntil: Long) {
        return withContext(dispatcher) {
            sharedPreference.putString(REFRESH_TOKEN, token)
            sharedPreference.putLong(REFRESH_TOKEN_VALID_UNTIL, validUntil)
            Timber.d("Refresh token stored, valid until $validUntil")
        }
    }

    override suspend fun storeUser(user: User) {
        return withContext(dispatcher) {
            sharedPreference.putString(USER_NAME, user.userName)
            sharedPreference.putString(USER_EMAIL, user.email)
        }
    }

    override suspend fun clearSession() {
        return withContext(dispatcher) { sharedPreference.clear() }
    }
}