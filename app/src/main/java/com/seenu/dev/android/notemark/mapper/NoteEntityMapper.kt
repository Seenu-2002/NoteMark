package com.seenu.dev.android.notemark.mapper

import com.seenu.dev.android.notemark.data.notes_repository.NoteEntity
import com.seenu.dev.android.notemark.domain.model.Note
import java.util.Date

fun NoteEntity.toDomain(): Note {
    return Note(
        id = this.id,
        title = this.title,
        content = this.content,
        createdAt = Date(this.createdAt),
        lastModified = Date(this.lastModified),
    )
}

fun Note.toEntity(): NoteEntity {
    return NoteEntity(
        id = this.id,
        title = this.title,
        content = this.content,
        createdAt = this.createdAt.time,
        lastModified = this.lastModified.time,
    )
}