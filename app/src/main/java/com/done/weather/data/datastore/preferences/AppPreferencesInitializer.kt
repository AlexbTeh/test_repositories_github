package com.done.weather.data.datastore.preferences

import android.content.Context
import com.done.weather.AppPreferences
import com.done.weather.data.PreferencesDto
import com.done.weather.domain.repository.BaseSettingsRepository
import com.done.weather.domain.repository.PreferencesRepository
import com.done.weather.domain.usecase.RestartAndClearAppUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import timber.log.Timber

class AppPreferencesInitializer(
    private val context: Context,
    private val baseSettingsRepository: BaseSettingsRepository,
    private val restartAndClearAppUseCase: RestartAndClearAppUseCase
) {
    companion object {
        private const val TAG = "AppPreferencesInitializer"
    }

    suspend fun invoke(preferences: PreferencesDto) = withContext(Dispatchers.IO) {
        updateServerAddress(preferences, context)
    }

    private suspend fun updateServerAddress(preferences: PreferencesDto, context: Context) =
        withContext(Dispatchers.Main) {
            if (preferences.serverAddress.isNotBlank()) {
                val oldServerAddress = baseSettingsRepository.serverAddress.first()
                preferences.serverAddress = preferences.serverAddress.replace("http://", "https://")
                if (preferences.serverAddress != oldServerAddress) {
                    Timber.tag("SettingsInitializer")
                        .d("Server address changed: $oldServerAddress -> ${preferences.serverAddress}")
                    baseSettingsRepository.updateServerAddress(preferences.serverAddress)
                    restartAndClearAppUseCase()

                    cancel()
                }
            }
        }
}
