package com.done.weather.di

import com.done.weather.AppPreferences
import com.done.weather.data.application.AppLifecycleManagerImpl
import com.done.weather.domain.application.AppLifecycleManager
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val otherModule = module {

    single<AppPreferences> {
        AppPreferences(androidApplication())
    }

    single<AppLifecycleManager> {
        AppLifecycleManagerImpl(
            context = androidApplication()
        )
    }
}