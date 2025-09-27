package com.seenu.dev.android.notemark.presentation.notes_list

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.seenu.dev.android.notemark.R
import com.seenu.dev.android.notemark.presentation.UiState
import com.seenu.dev.android.notemark.presentation.common.CREATE_NEW_NOTE_ID
import com.seenu.dev.android.notemark.presentation.common.models.NotesUiModel
import com.seenu.dev.android.notemark.presentation.notes_list.components.DeleteNoteConfirmationDialog
import com.seenu.dev.android.notemark.presentation.notes_list.components.GradientIconButton
import com.seenu.dev.android.notemark.presentation.notes_list.components.NotePreviewCard
import com.seenu.dev.android.notemark.presentation.notes_list.components.UserNameIcon
import com.seenu.dev.android.notemark.presentation.theme.fabGradientEnd
import com.seenu.dev.android.notemark.presentation.theme.fabGradientStart
import com.seenu.dev.android.notemark.presentation.util.DeviceConfiguration
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun NotesListScreen(userName: String, openNote: (id: Long) -> Unit = {}, openSettings: () -> Unit = {}) {

    val activity = LocalActivity.current ?: return
    val windowSizeClass = calculateWindowSizeClass(activity)
    val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)
    val viewModel: NotesListViewModel = koinViewModel()
    val notes by viewModel.notes.collectAsState()
    val noteToBeDeleted by viewModel.noteToBeDeleted.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getNotes()
    }

    Scaffold(
        modifier = Modifier,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                ),
                title = {
                    Text(text = stringResource(R.string.app_name))
                },
                actions = {
                    IconButton(onClick = openSettings) {
                        Icon(
                            painter = painterResource(R.drawable.ic_settings),
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    UserNameIcon(
                        name = userName,
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .size(40.dp)
                    )
                }
            )
        }, floatingActionButton = {
            val bottomPadding =
                if (deviceConfiguration == DeviceConfiguration.MOBILE_LANDSCAPE) 0.dp else 32.dp
            GradientIconButton(
                modifier = Modifier
                    .padding(end = 8.dp, bottom = bottomPadding)
                    .size(56.dp),
                onClick = {
                    openNote(CREATE_NEW_NOTE_ID)
                },
                shape = RoundedCornerShape(16.dp),
                gradient = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.fabGradientStart,
                        MaterialTheme.colorScheme.fabGradientEnd
                    )
                )
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Add"
                )
            }
        }) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .background(
                    color = MaterialTheme.colorScheme.surface,
                ),
            contentAlignment = Alignment.Center
        ) {

            when (val notesState = notes) {
                is UiState.Empty, is UiState.Loading -> {
                    CircularProgressIndicator()
                }

                is UiState.Error -> {
                    Timber.e("Error fetching notes: ${notesState.message}")
                    Text(
                        text = notesState.message ?: "Something went wrong",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                is UiState.Success -> {
                    NotesList(
                        modifier = Modifier.matchParentSize(),
                        deviceConfiguration = deviceConfiguration,
                        notes = notesState.data,
                        onNoteClick = { note ->
                            openNote(note.id)
                        },
                        onDeleteNote = { note ->
                            viewModel.showDeleteNoteConfirmationDialog(note)
                        }
                    )
                }
            }

            if (noteToBeDeleted != null) {
                DeleteNoteConfirmationDialog(
                    onDismissRequest = {
                        viewModel.resetNoteToBeDeleted()
                    },
                    onConfirmDiscard = {
                        noteToBeDeleted?.let {
                            viewModel.deleteNote(it.id)
                        }
                        viewModel.resetNoteToBeDeleted()
                    }
                )
            }
        }
    }
}

@Composable
fun NotesEmptyMessage(modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(60.dp))
        Text(
            text = stringResource(R.string.empty_notes_message),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotesList(
    modifier: Modifier = Modifier,
    deviceConfiguration: DeviceConfiguration,
    notes: List<NotesUiModel>,
    onNoteClick: (NotesUiModel) -> Unit = {},
    onDeleteNote: (NotesUiModel) -> Unit = {}
) {
    if (notes.isEmpty()) {
        NotesEmptyMessage(modifier)
    } else {
        val columnCount = when (deviceConfiguration) {
            DeviceConfiguration.MOBILE_LANDSCAPE -> 3
            else -> 2
        }

        LazyVerticalStaggeredGrid(
            modifier = Modifier
                .padding(4.dp),
            columns = StaggeredGridCells.Fixed(columnCount),
        ) {
            items(notes.size, key = {
                notes[it].id
            }) { index ->
                val note = notes[index]
                NotePreviewCard(
                    note = note,
                    modifier = Modifier
                        .padding(8.dp)
                        .animateItem(),
                    onClick = onNoteClick,
                    onLongPress = onDeleteNote
                )
            }
        }
    }
}