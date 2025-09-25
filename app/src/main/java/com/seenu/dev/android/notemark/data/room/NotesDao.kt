package com.seenu.dev.android.notemark.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.seenu.dev.android.notemark.data.notes_repository.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

    @Query("SELECT * FROM notes ORDER BY lastModified DESC")
    suspend fun getNotes(): List<NoteEntity> // TODO: Pagination

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNote(id: Long): NoteEntity?

    @Query("SELECT * FROM notes WHERE id = :id")
    fun getNoteFlow(id: Long): Flow<NoteEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNote(id: Long)

}