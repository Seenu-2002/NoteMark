package com.seenu.dev.android.notemark.presentation.note_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seenu.dev.android.notemark.domain.CreateEmptyNoteUseCase
import com.seenu.dev.android.notemark.domain.DeleteNoteUseCase
import com.seenu.dev.android.notemark.domain.GetNoteFlowUseCase
import com.seenu.dev.android.notemark.domain.UpdateNoteUseCase
import com.seenu.dev.android.notemark.presentation.UiState
import com.seenu.dev.android.notemark.presentation.common.CREATE_NEW_NOTE_ID
import com.seenu.dev.android.notemark.presentation.common.models.NotesUiModel
import com.seenu.dev.android.notemark.presentation.mapper.toDomain
import com.seenu.dev.android.notemark.presentation.mapper.toUiModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.util.Date

class NoteDetailViewModel : ViewModel(), KoinComponent {

    private val getNoteFlowUseCase by inject<GetNoteFlowUseCase>()
    private val updateNoteUseCase by inject<UpdateNoteUseCase>()
    private val createEmptyNoteUseCase by inject<CreateEmptyNoteUseCase>()
    private val deleteNoteUseCase by inject<DeleteNoteUseCase>()

    private val _note: MutableStateFlow<UiState<NotesUiModel>> = MutableStateFlow(UiState.Empty())
    val note: StateFlow<UiState<NotesUiModel>> = _note.asStateFlow()

    private val _isInReaderMode: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isInReaderMode: StateFlow<Boolean> = _isInReaderMode.asStateFlow()

    private val _shouldShowOtherElements: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val shouldShowOtherElements: StateFlow<Boolean> = _shouldShowOtherElements.asStateFlow()

    private val _isInEditMode: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isInEditMode: StateFlow<Boolean> = _isInEditMode.asStateFlow()

    private val _editNoteState: MutableStateFlow<EditNoteUiState?> = MutableStateFlow(null)
    val editNoteState: StateFlow<EditNoteUiState?> = _editNoteState.asStateFlow()

    private val _updateStatus: MutableSharedFlow<UiState<Unit>> = MutableSharedFlow()
    val updateStatus: SharedFlow<UiState<Unit>> = _updateStatus.asSharedFlow()

    private val _isInCreateMode: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isInCreateMode: StateFlow<Boolean> = _isInCreateMode.asStateFlow()

    fun init(noteId: Long) {
        viewModelScope.launch {
            if (noteId == CREATE_NEW_NOTE_ID) {
                Timber.d("Initializing NoteDetailViewModel in create mode")
                _isInCreateMode.value = true
                _isInEditMode.value = true
                createEmptyNote()
                return@launch
            }

            Timber.d("Initializing NoteDetailViewModel with noteId: $noteId")
            val oldNote = _note.value
            if (oldNote is UiState.Success && oldNote.data.id == noteId) {
                Timber.d("Note already loaded, skipping fetch")
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

                    if (_isInCreateMode.value) {
                        _editNoteState.value = EditNoteUiState(
                            title = note.title,
                            content = note.content
                        )
                    }
                } catch (exp: Exception) {
                    _note.value = UiState.Error(exp.message ?: "An error occurred")
                }
            }
        }
    }

    fun createEmptyNote() {
        viewModelScope.launch {
            Timber.d("Creating empty note")
            _note.value = UiState.Loading()
            val id = createEmptyNoteUseCase.invoke()
            init(id)
            Timber.d("Empty note created with id: $id")
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
                _isInCreateMode.value = false
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

    fun setReaderMode(isInReaderMode: Boolean) {
        _isInReaderMode.value = isInReaderMode
        showOtherElements(true)
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

    fun hasChanges(): Boolean {
        val note = (_note.value as? UiState.Success<NotesUiModel>)?.data
            ?: return false
        return _editNoteState.value?.let {
            it.title != note.title || it.content != note.content
        } ?: false
    }

    fun setShowDiscardChangesDialog(show: Boolean) {
        viewModelScope.launch {
            Timber.d("setShowDiscardChangesDialog: $show")
            val note = (_note.value as? UiState.Success<NotesUiModel>)?.data
                ?: return@launch
            _editNoteState.value?.let {
                val hasChanges = it.title != note.title || it.content != note.content

                Timber.d("Has changes: $hasChanges")
                if (hasChanges) {
                    _editNoteState.value =
                        _editNoteState.value?.copy(showDiscardChangesDialog = show)
                } else {
                    if (_isInCreateMode.value) {
                        Timber.d("Deleting note as no changes found and in create mode")
                        deleteNoteUseCase(note.id)
                    }
                    _editNoteState.value = null
                    _isInEditMode.value = false
                }
            } ?: run {
                Timber.d("No edit state found")
                if (_isInCreateMode.value) {
                    Timber.d("Deleting note as no edit state")
                    deleteNote(note.id)
                }

                _isInEditMode.value = false
                _editNoteState.value = null
            }
        }
    }

    private var showOtherElementsJob: Job? = null
    fun showOtherElements(show: Boolean) {
        _shouldShowOtherElements.value = show
        showOtherElementsJob?.cancel()
        if (show) {
            showOtherElementsJob = viewModelScope.launch {
                delay(5000)
                if (_shouldShowOtherElements.value && isActive) {
                    _shouldShowOtherElements.value = false
                }
            }
        }
    }


    fun deleteNote(id: Long) {
        viewModelScope.launch {
            deleteNoteUseCase(id)
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