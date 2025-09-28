package com.seenu.dev.android.notemark.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seenu.dev.android.notemark.data.User
import com.seenu.dev.android.notemark.domain.session.SessionManager
import com.seenu.dev.android.notemark.presentation.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import timber.log.Timber

class OnboardingViewModel constructor(
    private val sessionManager: SessionManager
) : ViewModel(), KoinComponent {

    private val _isUserLoggedIn: MutableStateFlow<UiState<User>> =
        MutableStateFlow(UiState.Empty())
    val isUserLoggedIn: StateFlow<UiState<User>> = _isUserLoggedIn.asStateFlow()

    fun checkIsUserLoggedIn() {
        viewModelScope.launch {
            _isUserLoggedIn.value = UiState.Loading()
            try {
                Timber.d("Checking if user is logged in")
                val isUserLoggedIn = sessionManager.isUserLoggedIn()
                Timber.d("Is user logged in: $isUserLoggedIn")
                if (isUserLoggedIn) {
                    val user = sessionManager.getUser()!!
                    _isUserLoggedIn.value = UiState.Success(user)
                } else {
                     // FIXME: Proper handlong
                    _isUserLoggedIn.value = UiState.Error("User not logged in")
                }
            } catch (exp: Exception) {
                Timber.e(exp, "Error checking if user is logged in")
                _isUserLoggedIn.value = UiState.Error(exp.message ?: "Failed to check login status")
            }
        }
    }

}