package com.seenu.dev.android.notemark.presentation

sealed interface UiState<T> {

    data class Loading<T> constructor(val data: T? = null) : UiState<T>
    data class Empty<T> constructor(val message: String? = null) : UiState<T>
    data class Success<T> constructor(val data: T) : UiState<T>
    data class Error<T> constructor(val message: String? = null, val data: T? = null) : UiState<T>

}