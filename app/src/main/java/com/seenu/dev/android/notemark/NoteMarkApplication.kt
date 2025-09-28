package com.seenu.dev.android.notemark

import android.app.Application
import com.seenu.dev.android.notemark.di.appModule
import com.seenu.dev.android.notemark.di.networkModule
import com.seenu.dev.android.notemark.di.securityModule
import io.kotzilla.sdk.analytics.koin.analytics
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class NoteMarkApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        startKoin {
            androidContext(this@NoteMarkApplication)
            analytics()
            androidLogger(level = Level.DEBUG)
            modules(securityModule, appModule, networkModule)
        }
    }

}