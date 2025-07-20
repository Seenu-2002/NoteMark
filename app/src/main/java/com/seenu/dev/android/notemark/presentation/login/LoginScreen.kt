package com.seenu.dev.android.notemark.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seenu.dev.android.notemark.presentation.UiState
import com.seenu.dev.android.notemark.presentation.login.components.LoginForm
import com.seenu.dev.android.notemark.presentation.login.components.LoginHeader
import com.seenu.dev.android.notemark.presentation.login.components.LoginPageField
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    onLogin: () -> Unit = {},
    onRegister: () -> Unit = {}
) {

    val viewmodel: LoginViewModel = koinViewModel()

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var enableLoginButton by rememberSaveable { mutableStateOf(false) }
    val loginState = viewmodel.loginState.collectAsStateWithLifecycle()

    var emailFieldError by rememberSaveable { mutableStateOf<ValidationError?>(null) }
    var passwordFieldError by rememberSaveable { mutableStateOf<ValidationError?>(null) }
    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }

    var focusedField by rememberSaveable { mutableStateOf<LoginPageField?>(null) }

    LaunchedEffect(loginState) {
        if (loginState.value is UiState.Success) {
            onLogin()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        contentWindowInsets = WindowInsets.statusBars
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                    shape = RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    )
                )
                .consumeWindowInsets(WindowInsets.statusBars)
        ) {

            LoginHeader(modifier = Modifier.padding(top = 8.dp))

            Spacer(modifier = Modifier.height(24.dp))

            LoginForm(
                modifier = Modifier.fillMaxSize(),
                email = email,
                password = password,
                isLoggingIn = loginState.value is UiState.Loading,
                emailFocusRequester = emailFocusRequester,
                passwordFocusRequester = passwordFocusRequester,
                enableLoginButton = enableLoginButton,
                emailValidationError = emailFieldError,
                passwordValidationError = passwordFieldError,
                onEmailChange = {
                    email = it
                    if (emailFieldError != null) {
                        emailFieldError = viewmodel.validateEmail(it)
                    }
                },
                onPasswordChange = {
                    password = it
                    if (passwordFieldError != null) {
                        passwordFieldError = viewmodel.validatePassword(it)
                    }
                },
                onLoginClick = {
                    viewmodel.login(
                        username = email,
                        password = password
                    )
                },
                onRegister = onRegister,
                onFocusChanged = { field, hasFocus ->
                    if (hasFocus) {
                        focusedField = field
                    } else if (focusedField == field) {
                        focusedField = null
                    }
                    when (field) {
                        LoginPageField.EMAIL -> {
                            if (email.isEmpty()) {
                                emailFieldError = null
                                return@LoginForm
                            }

                            emailFieldError = viewmodel.validateEmail(email)
                            if (emailFieldError == null) {
                                passwordFocusRequester.requestFocus()
                            }
                        }

                        LoginPageField.PASSWORD -> {
                            if (password.isEmpty()) {
                                passwordFieldError = null
                                return@LoginForm
                            }

                            passwordFieldError = viewmodel.validatePassword(password)
                            enableLoginButton = email.isNotEmpty() && password.isNotEmpty() &&
                                    emailFieldError == null && passwordFieldError == null
                        }
                    }
                }
            )

        }
    }

}