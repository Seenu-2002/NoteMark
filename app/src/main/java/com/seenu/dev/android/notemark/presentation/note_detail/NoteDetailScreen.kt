package com.seenu.dev.android.notemark.presentation.note_detail

import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seenu.dev.android.notemark.R
import com.seenu.dev.android.notemark.presentation.UiState
import com.seenu.dev.android.notemark.presentation.common.components.NotSupportedScreen
import com.seenu.dev.android.notemark.presentation.note_detail.components.DiscardChangesConfirmationDialog
import com.seenu.dev.android.notemark.presentation.note_detail.components.NoteDetailAction
import com.seenu.dev.android.notemark.presentation.note_detail.components.NoteDetailComponent
import com.seenu.dev.android.notemark.presentation.note_detail.components.NoteDetailFloatingActionBar
import com.seenu.dev.android.notemark.presentation.theme.titleXSmall
import com.seenu.dev.android.notemark.presentation.util.DeviceConfiguration
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun NoteDetailScreen(noteId: Long, onBack: () -> Unit) {

    val viewModel = koinViewModel<NoteDetailViewModel>()
    val note by viewModel.note.collectAsStateWithLifecycle()

    LaunchedEffect(noteId) {
        viewModel.init(noteId)
    }

    val isInCreateMode by viewModel.isInCreateMode.collectAsStateWithLifecycle()

    val activity = LocalActivity.current ?: return
    val windowSizeClass = calculateWindowSizeClass(activity)
    val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)

    val isInEditMode by viewModel.isInEditMode.collectAsStateWithLifecycle()
    val isInReaderMode by viewModel.isInReaderMode.collectAsStateWithLifecycle()

    val editNoteUiState by viewModel.editNoteState.collectAsStateWithLifecycle()

    var title by remember(note, editNoteUiState) {
        val editNoteUiState = editNoteUiState
        val text = editNoteUiState?.title ?: (note as? UiState.Success)?.data?.title.orEmpty()
        mutableStateOf(text)
    }
    var content by remember(note, editNoteUiState) {
        val editNoteUiState = editNoteUiState
        val text = editNoteUiState?.content ?: (note as? UiState.Success)?.data?.content.orEmpty()
        mutableStateOf(text)
    }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.updateStatus.collect { updateState ->
            if (updateState is UiState.Error) {
                Timber.e("Error updating note: ${updateState.message}")
                Toast.makeText(context, R.string.note_update_failed, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(), topBar = {
            if (deviceConfiguration == DeviceConfiguration.MOBILE_PORTRAIT) {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                    ),
                    title = {},
                    navigationIcon = {
                        if (!isInEditMode) {
                            TextButton(
                                onClick = onBack, colors = ButtonDefaults.textButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_chevron_left),
                                    contentDescription = "Back to all notes"
                                )
                                Text(
                                    text = stringResource(R.string.all_notes),
                                    style = MaterialTheme.typography.titleXSmall
                                )
                            }
                        } else {
                            IconButton(onClick = {
                                if (isInCreateMode && !viewModel.hasChanges()) {
                                    (note as? UiState.Success)?.data?.id?.let { id ->
                                        viewModel.deleteNote(id)
                                    }
                                    onBack() // FIXME: Back press dispatcher
                                } else {
                                    viewModel.setShowDiscardChangesDialog(true)
                                }
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_close),
                                    contentDescription = "Close Edit Mode"
                                )
                            }
                        }
                    },
                    actions = {
                        val hasChanges by remember(note, title, content) {
                            derivedStateOf {
                                (note as? UiState.Success)?.data?.let { noteData ->
                                    noteData.title != title || noteData.content != content
                                } ?: false
                            }
                        }

                        if (hasChanges && isInEditMode) {
                            TextButton(onClick = {
                                viewModel.updateNote()
                            }) {
                                Text(
                                    text = stringResource(R.string.save_note),
                                    style = MaterialTheme.typography.titleXSmall
                                )
                            }
                        }
                    }
                )
            }
        }, containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
            contentAlignment = Alignment.Center
        ) {

            when (val noteState = note) {
                is UiState.Loading, is UiState.Empty -> {
                    CircularProgressIndicator()
                }

                is UiState.Success -> {
                    val component = @Composable {
                        NoteDetailComponent(
                            modifier = Modifier.fillMaxSize(),
                            title = title,
                            content = content,
                            createdTime = noteState.data.createdAtFormattedWithTime,
                            lastEditedTime = noteState.data.lastModifiedFormattedWithTime,
                            isInEditMode = isInEditMode || isInCreateMode,
                            onTitleChange = viewModel::onTitleChange,
                            onContentChange = viewModel::onContentChange
                        )
                    }
                    when (deviceConfiguration) {
                        DeviceConfiguration.MOBILE_PORTRAIT -> {
                            component()
                        }

                        DeviceConfiguration.MOBILE_LANDSCAPE, DeviceConfiguration.TABLET_LANDSCAPE -> {
                            MobileLandscapeView(
                                modifier = Modifier.fillMaxSize(),
                                onBack = onBack
                            ) {
                                component()
                            }
                        }

                        else -> {
                            NotSupportedScreen(modifier = Modifier.fillMaxSize())
                        }
                    }

                    // No need to show edit / reader mode when the user while creating a new note
                    if (!isInCreateMode) {
                        val selectedAction = when {
                            isInEditMode -> NoteDetailAction.EDIT
                            isInReaderMode -> NoteDetailAction.READER_MODE
                            else -> NoteDetailAction.NONE
                        }
                        NoteDetailFloatingActionBar(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 24.dp),
                            selectedAction = selectedAction,
                            onActionClicked = { action ->
                                if (action == NoteDetailAction.EDIT) {
                                    viewModel.setEditMode(true)
                                } else if (action == NoteDetailAction.READER_MODE) {
                                    // TODO: Implement reader mode
                                }
                            }
                        )
                    }
                }

                is UiState.Error -> {
                    Text(
                        text = "Error loading note: ${noteState.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        if (editNoteUiState?.showDiscardChangesDialog == true) {
            DiscardChangesConfirmationDialog(
                onDismissRequest = {
                    viewModel.setShowDiscardChangesDialog(false)
                },
                onConfirmDiscard = {
                    viewModel.discardChanges()
                },
            )
        }
    }
}

@Composable
fun MobileLandscapeView(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    content: @Composable () -> Unit
) {
    Row(modifier = modifier) {
        Box(modifier = Modifier.weight(1F), contentAlignment = Alignment.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        painter = painterResource(R.drawable.ic_chevron_left),
                        contentDescription = "Back to all notes"
                    )
                }
                Text(
                    text = stringResource(R.string.all_notes),
                    style = MaterialTheme.typography.titleXSmall
                )
            }
        }
        content()
        Box(modifier = Modifier.weight(1F))
    }
}