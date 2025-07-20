package com.seenu.dev.android.notemark.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import com.seenu.dev.android.notemark.BuildConfig
import com.seenu.dev.android.notemark.domain.session.SessionManager
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.plugins.plugin
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module
import timber.log.Timber

val networkModule = module {
    single {
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        prettyPrint = true
                    }
                )
            }

            install(ResponseObserver) {
                onResponse { response ->
                    Timber.d("Response: ${response.request.url} -> ${response.status}")
                }
            }

            install(DefaultRequest) {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header("X-User-Email", BuildConfig.X_USER_EMAIL)
            }
        }.apply {
            this.plugin(HttpSend).intercept {
                val shouldAddAccessToken = it.url.pathSegments.lastOrNull() != "login"
                        && it.url.pathSegments.lastOrNull() != "register"
                        && it.url.pathSegments.lastOrNull() != "refresh"
                if (shouldAddAccessToken) {
                    val sessionManager: SessionManager = get()
                    val accessToken = sessionManager.getAccessToken()
                    if (accessToken != null) {
                        request {
                            header(HttpHeaders.Authorization, "Bearer $accessToken")
                        }
                    }
                }
                this.execute(it)
            }
        }
    }
}