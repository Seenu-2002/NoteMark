package com.seenu.dev.android.notemark.di

import com.seenu.dev.android.notemark.data.session.SessionManagerImpl
import com.seenu.dev.android.notemark.domain.session.SessionManager
import com.seenu.dev.android.notemark.utils.EncryptedSharedPreference
import com.seenu.dev.android.notemark.utils.SecurityUtil
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val securityModule = module {
    single<String>(qualifier = named("key_alias")) { "note_mark_session_key_alias" }
    single<SecurityUtil> { SecurityUtil(get(named("key_alias"))) }
    factory<EncryptedSharedPreference> { (prefName: String, mode: Int) ->
        EncryptedSharedPreference(
            prefName = prefName,
            mode = mode,
            context = get(),
            securityUtil = get(),
        )
    }
}