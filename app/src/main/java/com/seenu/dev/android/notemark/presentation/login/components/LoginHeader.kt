package com.seenu.dev.android.notemark.presentation.login.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.seenu.dev.android.notemark.R
import com.seenu.dev.android.notemark.presentation.theme.NoteMarkTheme

@Composable
fun LoginHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(
            8.dp
        )
    ) {
        Text(
            text = stringResource(R.string.login_header),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.login_message),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginHeaderPreview() {
    NoteMarkTheme {
        LoginHeader()
    }
}