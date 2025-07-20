package com.seenu.dev.android.notemark.data.auth_repository

enum class AuthError {
    INVALID_CREDENTIALS,
    USER_NOT_FOUND,
    USER_ALREADY_EXISTS,
    NETWORK_ERROR,
    UNKNOWN_ERROR
}

class AuthException constructor(val error: AuthError) : Exception() {
    override val message: String?
        get() = when (error) {
            AuthError.INVALID_CREDENTIALS -> "Invalid credentials provided."
            AuthError.USER_NOT_FOUND -> "User not found."
            AuthError.USER_ALREADY_EXISTS -> "User already exists."
            AuthError.NETWORK_ERROR -> "Network error occurred."
            AuthError.UNKNOWN_ERROR -> "An unknown error occurred."
        }
}