package com.seenu.dev.android.notemark.presentation.common.models

import java.util.Date

data class NotesUiModel constructor(
    val id: Long,
    val title: String,
    val content: String,
    val createdAt: Date,
    val createdAtFormatted: String,
    val createdAtFormattedWithTime: String,
    val lastModifiedAt: Date,
    val lastModifiedFormattedWithTime: String,
)