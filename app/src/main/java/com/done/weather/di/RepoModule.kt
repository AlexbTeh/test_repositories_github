package com.done.weather.di

import android.content.Context
import com.done.weather.data.repository.BaseSettingsRepositoryImpl
import com.done.weather.data.repository.CamifeyeRepositoryImpl
import com.done.weather.data.repository.GroupLogRepositoryImpl
import com.done.weather.data.repository.PreferencesRepositoryImpl
import com.done.weather.domain.repository.BaseSettingsRepository
import com.done.weather.domain.repository.GroupLogRepository
import com.done.weather.domain.repository.PreferencesRepository
import com.done.weather.domain.repository.CamifeyeRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repoModule = module {
    single<PreferencesRepository> {
        PreferencesRepositoryImpl(
            appPreferencesDatastore = get(
                named(
                    appPreferencesName
                )
            )
        )
    }
    single {
        androidContext().getSharedPreferences("group_log_prefs", Context.MODE_PRIVATE)
    }
    single<BaseSettingsRepository> { BaseSettingsRepositoryImpl(context = androidContext()) }


    single<CamifeyeRepository> {
        CamifeyeRepositoryImpl(
            settingsApi = get(),
            holeApi = get(),
            roundsApi = get()
        )
    }

    single<GroupLogRepository> { GroupLogRepositoryImpl(get()) }
}