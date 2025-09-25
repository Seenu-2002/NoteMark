package com.seenu.dev.android.notemark.presentation.note_detail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.seenu.dev.android.notemark.R
import com.seenu.dev.android.notemark.presentation.common.models.NotesUiModel
import com.seenu.dev.android.notemark.presentation.theme.NoteMarkTheme
import java.util.Date

@Preview(showBackground = true)
@Composable
private fun NoteDetailComponentPreview() {
    val note = NotesUiModel(
        id = 1L,
        title = "Sample Note",
        content = "This is a sample note content.",
        createdAt = Date(),
        createdAtFormatted = "22 JUN 2024",
        lastModifiedAt = Date(),
        lastModifiedFormattedWithTime = "22 JUN 2024 11:20",
        createdAtFormattedWithTime = "Just Now"
    )
    val (_, title, content, _, _, createdTime, _, lastEditedTime) = note
    NoteMarkTheme {
        NoteDetailComponent(
            modifier = Modifier.fillMaxSize(),
            title = title,
            content = content,
            createdTime = createdTime,
            lastEditedTime = lastEditedTime,
            isInEditMode = false,
            onTitleChange = {},
            onContentChange = {}
        )
    }
}

@Composable
fun NoteDetailComponent(
    modifier: Modifier = Modifier,
    title: String,
    content: String,
    createdTime: String,
    lastEditedTime: String,
    isInEditMode: Boolean,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        BasicTextField(
            readOnly = !isInEditMode,
            value = title,
            onValueChange = onTitleChange,
            textStyle = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    16.dp
                )
        )
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.surface)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.weight(1F)) {
                Text(
                    text = stringResource(R.string.date_created),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = createdTime,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Column(modifier = Modifier.weight(1F)) {
                Text(
                    text = stringResource(R.string.last_edited),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = lastEditedTime,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.surface)
        BasicTextField(
            readOnly = !isInEditMode,
            value = content,
            onValueChange = onContentChange,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
                .padding(16.dp),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
        )
    }
}