package com.seenu.dev.android.notemark.presentation.note_detail.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.seenu.dev.android.notemark.R
import com.seenu.dev.android.notemark.presentation.theme.NoteMarkTheme

@Preview
@Composable
private fun DiscardChangesConfirmationDialogPreview() {
    NoteMarkTheme {
        DiscardChangesConfirmationDialog()
    }
}

@Composable
fun DiscardChangesConfirmationDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = {},
    onConfirmDiscard: () -> Unit = {}
) {
    AlertDialog(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = stringResource(R.string.discard_changes),
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Text(
                text = stringResource(R.string.discard_changes_msg),
                style = MaterialTheme.typography.bodySmall
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirmDiscard) {
                Text(
                    text = stringResource(R.string.discard_confirm),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(
                    text = stringResource(R.string.discard_cancel),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        },
    )
}