package com.seenu.dev.android.notemark.domain

import com.seenu.dev.android.notemark.data.notes_repository.NotesRepository
import com.seenu.dev.android.notemark.domain.model.Note
import java.util.Date

class CreateEmptyNoteUseCase constructor(
    val repository: NotesRepository
) {

    suspend fun invoke(): Long {
        val note = Note(
            id = 0L,
            title = "",
            content = "",
            createdAt = Date(),
            lastModifiedAt = Date()
        )
        return repository.insertNote(note)
    }

}