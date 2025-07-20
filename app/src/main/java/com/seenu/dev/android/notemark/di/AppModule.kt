package com.seenu.dev.android.notemark.di

import com.seenu.dev.android.notemark.data.auth_repository.AuthRepository
import com.seenu.dev.android.notemark.data.auth_repository.AuthRepositoryImpl
import com.seenu.dev.android.notemark.data.session.SessionManagerImpl
import com.seenu.dev.android.notemark.domain.LoginUseCase
import com.seenu.dev.android.notemark.domain.session.SessionManager
import com.seenu.dev.android.notemark.presentation.login.LoginViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    single<SessionManager> {
        SessionManagerImpl(
            authRepository = get()
        )
    }
    single<AuthRepository> {
        AuthRepositoryImpl(httpClient = get())
    }
    viewModel {
        LoginViewModel()
    }
    factory<LoginUseCase> {
        LoginUseCase(
            repository = get(),
            sessionManager = get()
        )
    }
}