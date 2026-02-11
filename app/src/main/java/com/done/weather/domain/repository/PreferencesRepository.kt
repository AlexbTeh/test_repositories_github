package com.done.weather.domain.repository

import com.done.weather.data.datastore.preferences.entity.DisplayPreferencesEntity
import kotlinx.coroutines.flow.StateFlow

interface PreferencesRepository {
    val displayPreferences: StateFlow<DisplayPreferencesEntity>
    suspend fun updateDisplayPreferences(displayPreferences: DisplayPreferencesEntity)
    suspend fun updateDisplayPreferences(function: (DisplayPreferencesEntity) -> DisplayPreferencesEntity)
}