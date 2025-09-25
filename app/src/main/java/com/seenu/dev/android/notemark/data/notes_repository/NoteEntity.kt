package com.seenu.dev.android.notemark.data.notes_repository

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val title: String,
    val content: String,
    val createdAt: Long,
    val lastModified: Long
)