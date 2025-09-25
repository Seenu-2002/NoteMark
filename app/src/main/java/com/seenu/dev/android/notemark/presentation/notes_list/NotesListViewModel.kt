package com.seenu.dev.android.notemark.presentation.notes_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seenu.dev.android.notemark.domain.GetNotesUseCase
import com.seenu.dev.android.notemark.presentation.UiState
import com.seenu.dev.android.notemark.presentation.common.models.NotesUiModel
import com.seenu.dev.android.notemark.presentation.mapper.toUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NotesListViewModel : ViewModel(), KoinComponent {

    private val getNotesUseCase: GetNotesUseCase by inject()

    private val _notes: MutableStateFlow<UiState<List<NotesUiModel>>> = MutableStateFlow(UiState.Empty())
    val notes: StateFlow<UiState<List<NotesUiModel>>> = _notes.asStateFlow()

    fun getNotes() {
        viewModelScope.launch {
            _notes.value = UiState.Loading()
            try {
                val notesList = getNotesUseCase.getNotes().map {
                    it.toUiModel()
                }
                _notes.value = UiState.Success(notesList)
            } catch (e: Exception) {
                _notes.value = UiState.Error(e.message)
            }
        }

    }

}