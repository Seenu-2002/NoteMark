package com.seenu.dev.android.notemark.data.session.token

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse constructor(
    val refreshToken: String,
    val accessToken: String
)