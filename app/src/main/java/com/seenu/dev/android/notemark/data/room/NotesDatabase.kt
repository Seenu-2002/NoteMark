package com.seenu.dev.android.notemark.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.seenu.dev.android.notemark.data.notes_repository.NoteEntity

@Database(
    entities = [NoteEntity::class],
    version = 1
)
abstract class NotesDatabase : RoomDatabase() {

    abstract fun notesDao(): NotesDao

    companion object {
        const val DATABASE_NAME = "notes_database"
    }

}