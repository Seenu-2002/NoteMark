package com.seenu.dev.android.notemark.presentation.note_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seenu.dev.android.notemark.domain.GetNoteFlowUseCase
import com.seenu.dev.android.notemark.domain.UpdateNoteUseCase
import com.seenu.dev.android.notemark.presentation.UiState
import com.seenu.dev.android.notemark.presentation.common.models.NotesUiModel
import com.seenu.dev.android.notemark.presentation.mapper.toDomain
import com.seenu.dev.android.notemark.presentation.mapper.toUiModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.util.Date

class NoteDetailViewModel : ViewModel(), KoinComponent {

    private val getNoteFlowUseCase by inject<GetNoteFlowUseCase>()
    private val updateNoteUseCase by inject<UpdateNoteUseCase>()

    private val _note: MutableStateFlow<UiState<NotesUiModel>> = MutableStateFlow(UiState.Empty())
    val note: StateFlow<UiState<NotesUiModel>> = _note.asStateFlow()

    private val _isInReaderMode: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isInReaderMode: StateFlow<Boolean> = _isInReaderMode.asStateFlow()

    private val _isInEditMode: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isInEditMode: StateFlow<Boolean> = _isInEditMode.asStateFlow()

    private val _editNoteState: MutableStateFlow<EditNoteUiState?> = MutableStateFlow(null)
    val editNoteState: StateFlow<EditNoteUiState?> = _editNoteState.asStateFlow()

    private val _updateStatus: MutableSharedFlow<UiState<Unit>> = MutableSharedFlow()
    val updateStatus: SharedFlow<UiState<Unit>> = _updateStatus.asSharedFlow()

    fun init(noteId: Long) {
        viewModelScope.launch {
            val oldNote = _note.value
            if (oldNote is UiState.Success && oldNote.data.id == noteId) {
                return@launch
            }

            _note.value = UiState.Loading()

            getNoteFlowUseCase.getNoteFlow(noteId).collectLatest { note ->
                try {
                    if (note == null) {
                        throw Exception("Note not found") // FIXME: Proper error handling
                    }
                    val notesUiModel = note.toUiModel()
                    _note.value = UiState.Success(notesUiModel)
                } catch (exp: Exception) {
                    _note.value = UiState.Error(exp.message ?: "An error occurred")
                }
            }
        }
    }

    fun updateNote() {
        viewModelScope.launch {
            val note = _note.value as? UiState.Success<NotesUiModel> ?: return@launch
            val editState = _editNoteState.value ?: return@launch

            val updatedNote = note.data.copy(
                title = editState.title,
                content = editState.content,
                lastModifiedAt = Date()
            ).toDomain()
            _updateStatus.emit(UiState.Loading())
            try {
                updateNoteUseCase.invoke(updatedNote)
                _updateStatus.emit(UiState.Success(Unit))
            } catch (exp: Exception) {
                Timber.e(exp, "Error updating note")
                _updateStatus.emit(UiState.Error(exp.message ?: "An error occurred"))
            } finally {
                _editNoteState.value = null
                _isInEditMode.value = false
            }
        }
    }

    fun setEditMode(isInEditMode: Boolean) {
        _isInEditMode.value = isInEditMode
        if (isInEditMode) {
            _isInReaderMode.value = false
            if (_note.value is UiState.Success) {
                val note = (_note.value as UiState.Success<NotesUiModel>).data
                _editNoteState.value = EditNoteUiState(
                    title = note.title,
                    content = note.content
                )
            }
        }
    }

    fun onTitleChange(title: String) {
        if (_editNoteState.value != null) {
            _editNoteState.value = _editNoteState.value?.copy(title = title)
        }
    }

    fun onContentChange(content: String) {
        if (_editNoteState.value != null) {
            _editNoteState.value = _editNoteState.value?.copy(content = content)
        }
    }

    fun setShowDiscardChangesDialog(show: Boolean) {
        _editNoteState.value?.let {
            val note = (_note.value as? UiState.Success<NotesUiModel>)?.data
                ?: return
            val hasChanges = it.title != note.title || it.content != note.content

            if (hasChanges) {
                _editNoteState.value = _editNoteState.value?.copy(showDiscardChangesDialog = show)
            } else {
                _editNoteState.value = null
                _isInEditMode.value = false
            }
        } ?: run {
            _isInEditMode.value = false
            _editNoteState.value = null
        }
    }

    fun discardChanges() {
        _editNoteState.value = null
        _isInEditMode.value = false
    }

}

data class EditNoteUiState constructor(
    val title: String,
    val content: String,
    val showDiscardChangesDialog: Boolean = false
)