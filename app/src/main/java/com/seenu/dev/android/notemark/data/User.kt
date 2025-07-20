package com.seenu.dev.android.notemark.data

import kotlinx.serialization.Serializable

@Serializable
data class User constructor(
    val userName: String,
    val email: String
)
