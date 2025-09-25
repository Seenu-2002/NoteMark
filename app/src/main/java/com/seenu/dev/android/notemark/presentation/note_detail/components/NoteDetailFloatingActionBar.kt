package com.seenu.dev.android.notemark.presentation.note_detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.seenu.dev.android.notemark.R
import com.seenu.dev.android.notemark.presentation.theme.NoteMarkTheme

enum class NoteDetailAction {
    NONE, EDIT, READER_MODE
}

@Preview
@Composable
private fun NoteDetailFloatingActionBarPreview() {
    NoteMarkTheme {
        NoteDetailFloatingActionBar(selectedAction = NoteDetailAction.EDIT)
    }
}

@Composable
fun NoteDetailFloatingActionBar(
    modifier: Modifier = Modifier,
    selectedAction: NoteDetailAction = NoteDetailAction.NONE,
    onActionClicked: (action: NoteDetailAction) -> Unit = {},
) {
    val selectedIconModifier = Modifier.background(
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1F),
        shape = MaterialTheme.shapes.medium
    )

    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium
            )
            .padding(4.dp)
    ) {
        IconButton(
            modifier = if (selectedAction == NoteDetailAction.EDIT) selectedIconModifier else Modifier,
            onClick = {
                onActionClicked(NoteDetailAction.EDIT)
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_edit),
                contentDescription = "Edit Note",
                tint = if (selectedAction == NoteDetailAction.EDIT) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
        IconButton(
            modifier = if (selectedAction == NoteDetailAction.READER_MODE) selectedIconModifier else Modifier,
            onClick = {
                onActionClicked(NoteDetailAction.READER_MODE)
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_reader_mode),
                contentDescription = "Reader Mode",
                tint = if (selectedAction == NoteDetailAction.READER_MODE) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}