package com.done.weather.di

import com.done.weather.data.datastore.preferences.AppPreferencesInitializer
import com.done.weather.data.datastore.settings.AppSettingsDatastore
import com.done.weather.data.datastore.settings.appSettingsDatastore
import com.done.weather.data.datastore.preferences.AppPreferencesDatastore
import com.done.weather.data.datastore.preferences.appPreferencesDatastore
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val appPreferencesName: String = "appPreferences"

val datastoreModule = module {
    single<AppPreferencesDatastore>(named(appPreferencesName)) { androidApplication().appPreferencesDatastore }
    single<AppSettingsDatastore> { androidContext().appSettingsDatastore }
    single<AppPreferencesInitializer> {
        AppPreferencesInitializer(
            context = androidApplication(),
            baseSettingsRepository = get(),
            restartAndClearAppUseCase = get()
        )
    }}