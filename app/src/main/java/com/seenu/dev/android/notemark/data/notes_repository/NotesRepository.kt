package com.seenu.dev.android.notemark.data.notes_repository

import com.seenu.dev.android.notemark.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NotesRepository {

    suspend fun getNotes(): List<Note>

    suspend fun getNoteById(id: Long): Note?
    suspend fun getNoteByIdFlow(id: Long): Flow<Note?>

    suspend fun insertNote(note: Note)
    suspend fun updateNote(note: Note)

    suspend fun deleteNote(id: Long)

}