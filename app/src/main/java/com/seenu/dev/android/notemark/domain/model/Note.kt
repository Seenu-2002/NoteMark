package com.seenu.dev.android.notemark.domain.model

import java.util.Date


data class Note constructor(
    val id: Long,
    val title: String,
    val content: String,
    val createdAt: Date,
    val lastModified: Date,
)