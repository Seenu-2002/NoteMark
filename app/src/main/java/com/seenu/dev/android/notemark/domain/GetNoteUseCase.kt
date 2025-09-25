package com.seenu.dev.android.notemark.domain

import com.seenu.dev.android.notemark.data.notes_repository.NotesRepository

class GetNoteUseCase constructor(
    private val repository: NotesRepository
) {

    suspend fun getNote(noteId: Long) = repository.getNoteById(noteId)

}