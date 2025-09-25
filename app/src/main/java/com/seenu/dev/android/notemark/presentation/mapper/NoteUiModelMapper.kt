package com.seenu.dev.android.notemark.presentation.mapper

import com.seenu.dev.android.notemark.domain.model.Note
import com.seenu.dev.android.notemark.presentation.common.models.NotesUiModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.minutes

fun Note.toUiModel(): NotesUiModel {
    return NotesUiModel(
        id = this.id,
        title = this.title,
        content = this.content,
        createdAt = this.createdAt,
        createdAtFormatted = mapDate(this.createdAt),
        createdAtFormattedWithTime = mapDateWithTime(this.createdAt),
        lastModifiedAt = this.lastModified,
        lastModifiedFormattedWithTime = mapDateWithTime(this.lastModified)
    )
}

fun NotesUiModel.toDomain(): Note {
    return Note(
        id = this.id,
        title = this.title,
        content = this.content,
        createdAt = this.createdAt,
        lastModified = this.lastModifiedAt
    )
}

private fun mapDate(createdDate: Date): String {
    val createdDateCalendar = Calendar.getInstance()
    createdDateCalendar.time = createdDate
    val year = createdDateCalendar.get(Calendar.YEAR)
    val todayCalendar = Calendar.getInstance()
    val todayYear = todayCalendar.get(Calendar.YEAR)
    val pattern = if (year == todayYear) {
        "dd MMM"
    } else {
        "dd MMM yyyy"
    }

    return SimpleDateFormat(pattern, Locale.getDefault()).format(createdDate).uppercase()
}

private fun mapDateWithTime(date: Date): String {
    val currentTime = System.currentTimeMillis()
    if (date.time + 5.minutes.inWholeMilliseconds > currentTime) { // time within the last 5 minutes
        return "Just Now"
    }

    val pattern = "dd MMM yyyy HH:mm"
    return SimpleDateFormat(pattern, Locale.getDefault()).format(date).uppercase()
}