package com.seenu.dev.android.notemark.data.auth_repository

import com.seenu.dev.android.notemark.data.User
import com.seenu.dev.android.notemark.data.session.token.TokenResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.path
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import timber.log.Timber

class AuthRepositoryImpl constructor(
    private val httpClient: HttpClient
) : AuthRepository {

    override suspend fun register(user: User, password: String) {
        return withContext(Dispatchers.IO) {
            val response = httpClient.post {
                url {
                    path("/api/auth/register")
                }
                setBody(buildJsonObject {
                    put("username", JsonPrimitive(user.userName))
                    put("email", JsonPrimitive(user.email))
                    put("password", JsonPrimitive(password))
                })
            }

            if (response.status.value != 200) {
                throw Exception("Registration failed with status: ${response.status.value}")
            }

            when (response.status.value) {
                200 -> null // Registration successful
                409 -> {
                    AuthException(AuthError.USER_ALREADY_EXISTS)
                }

                400 -> {
                    AuthException(AuthError.INVALID_CREDENTIALS)
                }

                else -> {
                    AuthException(AuthError.UNKNOWN_ERROR)
                }
            }
        }
    }

    override suspend fun login(email: String, password: String): TokenResponse {
        return withContext(Dispatchers.IO) {
            val response = httpClient.post {
                url {
                    path("/api/auth/login")
                }
                setBody(buildJsonObject {
                    put("email", JsonPrimitive(email))
                    put("password", JsonPrimitive(password))
                })
            }

            when (response.status.value) {
                200 -> {
                    response.body<TokenResponse>()
                }

                401 -> {
                    throw AuthException(AuthError.INVALID_CREDENTIALS)
                }

                404 -> {
                    throw AuthException(AuthError.USER_NOT_FOUND)
                }

                else -> {
                    Timber.e("Login failed with status: ${response.status.value} :: ${response.body<String>()}")
                    throw AuthException(AuthError.UNKNOWN_ERROR)
                }
            }
        }
    }

    override suspend fun refreshToken(token: String): TokenResponse {
        return withContext(Dispatchers.IO) {
            val response = httpClient.post {
                url {
                    path("/api/auth/refresh")
                }
                setBody(buildJsonObject {
                    put("refreshToken", JsonPrimitive(token))
                })
            }

            when (response.status.value) {
                200 -> {
                    response.body<TokenResponse>()
                }

                401 -> {
                    throw AuthException(AuthError.INVALID_CREDENTIALS)
                }

                else -> {
                    throw AuthException(AuthError.UNKNOWN_ERROR)
                }
            }
        }
    }

}