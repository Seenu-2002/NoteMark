package com.seenu.dev.android.notemark.di

import androidx.room.Room
import com.seenu.dev.android.notemark.data.auth_repository.AuthRepository
import com.seenu.dev.android.notemark.data.auth_repository.AuthRepositoryImpl
import com.seenu.dev.android.notemark.data.notes_repository.NotesRepository
import com.seenu.dev.android.notemark.data.notes_repository.NotesRepositoryImpl
import com.seenu.dev.android.notemark.data.room.NotesDatabase
import com.seenu.dev.android.notemark.data.session.SessionManagerImpl
import com.seenu.dev.android.notemark.domain.GetNoteFlowUseCase
import com.seenu.dev.android.notemark.domain.GetNoteUseCase
import com.seenu.dev.android.notemark.domain.GetNotesUseCase
import com.seenu.dev.android.notemark.domain.LoginUseCase
import com.seenu.dev.android.notemark.domain.UpdateNoteUseCase
import com.seenu.dev.android.notemark.domain.session.SessionManager
import com.seenu.dev.android.notemark.presentation.login.LoginViewModel
import com.seenu.dev.android.notemark.presentation.note_detail.NoteDetailViewModel
import com.seenu.dev.android.notemark.presentation.notes_list.NotesListViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<NotesDatabase> {
        val context = androidContext()
        Room.databaseBuilder(
            context,
            NotesDatabase::class.java,
            NotesDatabase.DATABASE_NAME
        ).build()
    }
    factory {
        get<NotesDatabase>().notesDao()
    }
    single<SessionManager> {
        SessionManagerImpl(
            authRepository = get()
        )
    }
    single<AuthRepository> {
        AuthRepositoryImpl(httpClient = get())
    }
    single<NotesRepository> {
        NotesRepositoryImpl(
            get()
        )
    }
    viewModel {
        LoginViewModel()
    }
    viewModel {
        NotesListViewModel()
    }
    viewModel {
        NoteDetailViewModel()
    }
    single {
        LoginUseCase(
            repository = get(),
            sessionManager = get()
        )
    }
    single {
        GetNotesUseCase(
            repository = get()
        )
    }
    single {
        GetNoteUseCase(
            repository = get()
        )
    }
    single {
        GetNoteFlowUseCase(
            repository = get()
        )
    }
    single {
        UpdateNoteUseCase(
            repository = get()
        )
    }
}