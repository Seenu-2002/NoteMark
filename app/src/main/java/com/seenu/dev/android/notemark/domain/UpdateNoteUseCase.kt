package com.seenu.dev.android.notemark.domain

import com.seenu.dev.android.notemark.data.notes_repository.NotesRepository
import com.seenu.dev.android.notemark.domain.model.Note

class UpdateNoteUseCase constructor(
    private val repository: NotesRepository
) {

    suspend fun invoke(note: Note) = repository.updateNote(note)

}