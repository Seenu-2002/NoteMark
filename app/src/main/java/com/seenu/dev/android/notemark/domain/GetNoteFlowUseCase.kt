package com.seenu.dev.android.notemark.domain

import com.seenu.dev.android.notemark.data.notes_repository.NotesRepository

class GetNoteFlowUseCase constructor(
    private val repository: NotesRepository
) {

    suspend fun getNoteFlow(noteId: Long) = repository.getNoteByIdFlow(noteId)

}