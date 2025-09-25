package com.seenu.dev.android.notemark.presentation.route

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {

    @Serializable
    data object Login : Screen

    @Serializable
    data object Register : Screen

    @Serializable
    data object Onboarding: Screen

    @Serializable
    data class NotesList constructor(val userName: String): Screen

    @Serializable
    data class NoteDetail constructor(val noteId: Long): Screen

    @Serializable
    data object NoteCreate: Screen

    @Serializable
    data object NoteEdit: Screen

}