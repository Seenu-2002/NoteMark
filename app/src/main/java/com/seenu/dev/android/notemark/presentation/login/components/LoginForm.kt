package com.seenu.dev.android.notemark.presentation.login.components

import android.R.attr.label
import android.R.attr.password
import android.R.attr.text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.seenu.dev.android.notemark.R
import com.seenu.dev.android.notemark.presentation.login.ValidationError
import com.seenu.dev.android.notemark.presentation.login.components.FormTextField
import com.seenu.dev.android.notemark.presentation.theme.NoteMarkTheme

@Composable
fun LoginForm(
    modifier: Modifier = Modifier,
    email: String,
    password: String,
    enableLoginButton: Boolean,
    emailFocusRequester: FocusRequester,
    passwordFocusRequester: FocusRequester,
    isLoggingIn: Boolean = false,
    emailValidationError: ValidationError? = null,
    passwordValidationError: ValidationError? = null,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRegister: () -> Unit,
    onLoginClick: () -> Unit,
    onFocusChanged: (field: LoginPageField, hasFocus: Boolean) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current

    Column(modifier = modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {

        FormTextField(
            label = stringResource(R.string.email),
            value = email,
            modifier = Modifier
                .focusRequester(emailFocusRequester)
                .onFocusChanged { focusState ->
                    onFocusChanged(LoginPageField.EMAIL, focusState.hasFocus)
                },
            placeHolder = {
                Text(
                    stringResource(R.string.email_placeholder)
                )
            },
            onValueChange = onEmailChange,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            isError = emailValidationError != null,
            errorMessage = emailValidationError?.let {
                context.getString(it.messageRes)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))


        var showPassword: Boolean by rememberSaveable {
            mutableStateOf(false)
        }

        FormTextField(
            label = stringResource(R.string.password),
            value = password,
            modifier = Modifier
                .focusRequester(passwordFocusRequester)
                .onFocusChanged { focusState ->
                    onFocusChanged(LoginPageField.PASSWORD, focusState.hasFocus)
                },
            placeHolder = {
                Text(
                    stringResource(R.string.password)
                )
            },
            onValueChange = onPasswordChange,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = if (showPassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation(mask = '*')
            },
            trailingIcon = {
                val iconRes = if (showPassword) {
                    R.drawable.ic_eye
                } else {
                    R.drawable.ic_eye_off
                }
                IconButton(onClick = {
                    showPassword = !showPassword
                }) {
                    Icon(
                        painter = painterResource(iconRes),
                        contentDescription = "Toggle Password Visibility"
                    )
                }
            },
            isError = passwordValidationError != null,
            errorMessage = passwordValidationError?.let {
                context.getString(it.messageRes)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (isLoggingIn) {
            Box(modifier = Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            Button(
                enabled = enableLoginButton,
                onClick = onLoginClick,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    modifier = Modifier.padding(vertical = 4.dp),
                    text = stringResource(R.string.login_btn),
                    style = MaterialTheme.typography.titleSmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .clickable(onClick = onRegister),
                text = stringResource(R.string.dont_have_account),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

enum class LoginPageField {
    EMAIL,
    PASSWORD
}

@Preview
@Composable
private fun LoginFormPreview() {
    NoteMarkTheme {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        LoginForm(
            modifier = Modifier, email = email, password = password,
            emailFocusRequester = remember { FocusRequester() },
            passwordFocusRequester = remember { FocusRequester() },
            enableLoginButton = email.isNotEmpty() && password.isNotEmpty(),
            onEmailChange = { email = it },
            onPasswordChange = { password = it },
            onRegister = {},
            onLoginClick = {}
        )
    }
}