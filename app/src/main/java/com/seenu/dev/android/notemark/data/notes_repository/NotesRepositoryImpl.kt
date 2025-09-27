package com.seenu.dev.android.notemark.data.notes_repository

import com.seenu.dev.android.notemark.data.room.NotesDao
import com.seenu.dev.android.notemark.domain.model.Note
import com.seenu.dev.android.notemark.mapper.toDomain
import com.seenu.dev.android.notemark.mapper.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class NotesRepositoryImpl constructor(
    private val notesDao: NotesDao
) : NotesRepository {

    override fun getNotesFlow(): Flow<List<Note>> {
        return notesDao.getNotesFlow().map { it.map { noteEntity -> noteEntity.toDomain() } }
    }

    override suspend fun getNoteById(id: Long): Note? {
        return withContext(Dispatchers.IO){
            notesDao.getNote(id)?.toDomain() ?: TODO() // throw exception
        }
    }

    override suspend fun getNoteByIdFlow(id: Long): Flow<Note?> {
        return withContext(Dispatchers.IO) {
            notesDao.getNoteFlow(id).map { it?.toDomain() }
        }
    }

    override suspend fun insertNote(note: Note): Long {
        return withContext(Dispatchers.IO) {
            notesDao.insertNote(note.toEntity())
        }
    }

    override suspend fun updateNote(note: Note) {
        return withContext(Dispatchers.IO) {
            notesDao.updateNote(note.toEntity())
        }
    }

    override suspend fun deleteNote(id: Long) {
        return withContext(Dispatchers.IO) {
            notesDao.deleteNote(id)
        }
    }

}