package com.seenu.dev.android.notemark.presentation.login

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seenu.dev.android.notemark.R
import com.seenu.dev.android.notemark.data.User
import com.seenu.dev.android.notemark.domain.LoginUseCase
import com.seenu.dev.android.notemark.presentation.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class LoginViewModel constructor() : ViewModel(), KoinComponent {

    private val loginUseCase: LoginUseCase by inject()

    private val _loginState: MutableStateFlow<UiState<User>> = MutableStateFlow(UiState.Error())
    val loginState: StateFlow<UiState<User>> = _loginState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            Timber.d("Attempting login for user")
            _loginState.value = UiState.Loading()
            try {
                val user = loginUseCase.login(email, password)
                Timber.d("Login successful")
                _loginState.value = UiState.Success(user)
            } catch (e: Exception) {
                Timber.e(e, "Login failed")
                _loginState.value = UiState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun validateEmail(email: String): ValidationError? {
        val isValid =
            email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        return if (!isValid) {
            ValidationError.INVALID_EMAIL_ADDRESS
        } else {
            null
        }
    }

    fun validatePassword(password: String): ValidationError? {
        return when {
            password.length < 8 -> ValidationError.PASSWORD_SHOULD_BE_MORE_THAN_8_CHARACTERS
            else -> null
        }
    }

}

enum class ValidationError(@StringRes val messageRes: Int) {
    USER_NAME_SHOULD_BE_MORE_THAN_3_CHARACTERS(R.string.user_name_less_than_3),
    USER_NAME_SHOULD_NOT_BE_MORE_THAN_20_CHARACTERS(R.string.user_name_not_more_than_20),
    PASSWORD_SHOULD_BE_MORE_THAN_8_CHARACTERS(R.string.password_less_than_8),
    INVALID_EMAIL_ADDRESS(R.string.invalid_email_address),
    PASSWORD_DO_NOT_MATCH(R.string.password_do_not_match),
}