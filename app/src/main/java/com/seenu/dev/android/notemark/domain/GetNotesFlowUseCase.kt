package com.seenu.dev.android.notemark.domain

import com.seenu.dev.android.notemark.data.notes_repository.NotesRepository

class GetNotesFlowUseCase constructor(
    private val repository: NotesRepository
) {

    suspend fun getNotes() = repository.getNotesFlow()

}