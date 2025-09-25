package com.seenu.dev.android.notemark.domain

import com.seenu.dev.android.notemark.data.notes_repository.NotesRepository

class DeleteNoteUseCase constructor(
    private val repository: NotesRepository
) {

    suspend operator fun invoke(id: Long) {
        repository.deleteNote(id)
    }

}