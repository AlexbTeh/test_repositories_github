package com.done.weather.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.done.weather.AppConstant
import com.done.weather.BuildConfig
import com.done.weather.domain.repository.BaseSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class BaseSettingsRepositoryImpl(private val context: Context) : BaseSettingsRepository {
    companion object {
        private val Context.baseDatastore: DataStore<Preferences> by preferencesDataStore(
            AppConstant.BASE_SETTINGS_NAME
        )

        val SERVER_ADDRESS_KEY = stringPreferencesKey("base_url_key")
        val BEARER_TOKEN_KEY = stringPreferencesKey("bearer_token_key")
        val APP_VERSION_CODE_KEY = stringPreferencesKey("app_version_code_key")
    }

    override val serverAddress: Flow<String> = context.baseDatastore.data.map {
        it[SERVER_ADDRESS_KEY] ?: BuildConfig.BASE_URL
    }

    override suspend fun updateServerAddress(serverAddress: String) {
        withContext(Dispatchers.IO) {
            context.baseDatastore.edit { preferences ->
                preferences[SERVER_ADDRESS_KEY] = serverAddress
            }
        }
    }

    override val bearerToken: Flow<String> = context.baseDatastore.data.map {
        it[BEARER_TOKEN_KEY] ?: ""
    }

    override suspend fun updateBearerToken(bearerToken: String) {
        withContext(Dispatchers.IO) {
            context.baseDatastore.edit { preferences ->
                preferences[BEARER_TOKEN_KEY] = bearerToken
            }
        }
    }

    override val appVersionCode: Flow<Int> = context.baseDatastore.data.map {
        runCatching { it[APP_VERSION_CODE_KEY]?.toInt() }.getOrNull() ?: -1
    }

    override suspend fun updateAppVersionCode(version: Int) {
        withContext(Dispatchers.IO) {
            context.baseDatastore.edit { preferences ->
                preferences[APP_VERSION_CODE_KEY] = version.toString()
            }
        }
    }
}
