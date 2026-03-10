package com.done.weather.data.repository

import com.done.weather.domain.repository.PreferencesRepository
import com.done.weather.data.datastore.preferences.AppPreferencesDatastore
import com.done.weather.data.datastore.preferences.entity.DisplayPreferencesEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

class PreferencesRepositoryImpl(private val appPreferencesDatastore: AppPreferencesDatastore) :
    PreferencesRepository {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override val displayPreferences: StateFlow<DisplayPreferencesEntity> =
        appPreferencesDatastore.data.map { it.displayPreferences }.stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = DisplayPreferencesEntity()
        )

    override suspend fun updateDisplayPreferences(displayPreferences: DisplayPreferencesEntity) {
        withContext(Dispatchers.IO) {
            appPreferencesDatastore.updateData { it.copy(displayPreferences = displayPreferences) }
        }
    }

    override suspend fun updateDisplayPreferences(function: (DisplayPreferencesEntity) -> DisplayPreferencesEntity) {
        val demoPreferences = function(displayPreferences.value)

        updateDisplayPreferences(demoPreferences)
    }
}