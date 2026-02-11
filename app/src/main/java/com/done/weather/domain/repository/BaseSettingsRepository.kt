package com.done.weather.domain.repository

import kotlinx.coroutines.flow.Flow

interface BaseSettingsRepository {
    val serverAddress: Flow<String>
    suspend fun updateServerAddress(serverAddress: String)

    val bearerToken: Flow<String>
    suspend fun updateBearerToken(bearerToken: String)

    val appVersionCode: Flow<Int>
    suspend fun updateAppVersionCode(version: Int)
}