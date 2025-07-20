package com.seenu.dev.android.notemark.presentation.login.components

import android.R.attr.textStyle
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seenu.dev.android.notemark.R
import com.seenu.dev.android.notemark.presentation.theme.NoteMarkTheme

@Composable
fun FormTextField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null,
    focusRequester: FocusRequester? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    placeHolder: @Composable () -> Unit = {},
    onValueChange: (String) -> Unit = {}
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 15.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        val shape = RoundedCornerShape(12.dp)
        var hasFocus by remember {
            mutableStateOf(false)
        }
        val outlineColor = when {
            isError -> {
                MaterialTheme.colorScheme.error
            }

            hasFocus -> {
                MaterialTheme.colorScheme.primary
            }

            else -> {
                Color.Transparent
            }
        }

        TextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            placeholder = placeHolder,
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            shape = shape,
            textStyle = MaterialTheme.typography.bodyLarge,
            keyboardOptions = keyboardOptions,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .border(
                    1.dp,
                    outlineColor,
                    shape
                )
                .clip(shape)
                .onFocusChanged {
                    hasFocus = it.hasFocus
                }
        )
        if (isError) {
            Text(
                text = errorMessage ?: "",
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Preview
@Composable
private fun FormTextFieldPreview() {
    NoteMarkTheme {
        var value by remember {
            mutableStateOf("")
        }
        FormTextField(
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(R.string.email),
            value = value,
            placeHolder = {
                Text("john.doe@example.com")
            }
        ) {
            value = it
        }
    }
}

@Preview
@Composable
private fun FormTextFieldErrorPreview() {
    NoteMarkTheme {
        var value by remember {
            mutableStateOf("")
        }
        FormTextField(
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(R.string.email),
            value = value,
            placeHolder = {
                Text("john.doe@example.com")
            },
            isError = true,
            errorMessage = "Email is not valid one!"
        ) {
            value = it
        }
    }
}