package com.seenu.dev.android.notemark.presentation.notes_list.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.seenu.dev.android.notemark.presentation.common.models.NotesUiModel
import com.seenu.dev.android.notemark.presentation.theme.NoteMarkTheme
import java.util.Date

@Preview
@Composable
private fun NotePreviewCardPreview() {
    val note = NotesUiModel(
        id = 1L,
        title = "Sample Note",
        content = "This is a sample note content that is long enough to demonstrate the truncation feature in the note preview card.",
        createdAt = Date(),
        createdAtFormatted = "Oct 10, 2023",
        lastModifiedAt = Date(),
        createdAtFormattedWithTime = "Oct 10, 2023, 10:00 AM",
        lastModifiedFormattedWithTime = "Oct 10, 2023, 10:00 AM",
    )


    NoteMarkTheme {
        NotePreviewCard(
            note = note,
            modifier = Modifier.width(200.dp)
        )
    }
}

@Preview
@Composable
private fun NotePreviewCardPreviewPreviousYear() {
    val note = NotesUiModel(
        id = 1L,
        title = "Sample Note",
        content = "This is a sample note content that is long enough to demonstrate the truncation feature in the note preview card.",
        createdAt = Date(123, 9, 10), // Oct 10, 2023
        createdAtFormatted = "Oct 10, 2023",
        lastModifiedAt = Date(123, 9, 10),
        createdAtFormattedWithTime = "Oct 10, 2023, 10:00 AM",
        lastModifiedFormattedWithTime = "Oct 10, 2023, 10:00 AM",
    )

    NoteMarkTheme {
        NotePreviewCard(
            note = note,
            modifier = Modifier.width(200.dp)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotePreviewCard(
    modifier: Modifier = Modifier,
    note: NotesUiModel,
    maxCharacterCount: Int = 150,
    onClick: (NotesUiModel) -> Unit = {},
    onLongPress: (NotesUiModel) -> Unit = {},
) {

    Column(
        modifier = modifier
            .clip(shape = MaterialTheme.shapes.medium)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                shape = MaterialTheme.shapes.medium
            )
            .combinedClickable(onLongClick = {
                onLongPress(note)

            }, onClick = {
                onClick(note)
            })
            .padding(12.dp)
    ) {
        Text(
            text = note.createdAtFormatted,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = note.title, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        val content = if (note.content.length > maxCharacterCount) {
            note.content.take(maxCharacterCount) + "..."
        } else {
            note.content
        }
        Text(
            text = content,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
