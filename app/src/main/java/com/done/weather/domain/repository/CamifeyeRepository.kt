package com.done.weather.domain.repository

import com.done.weather.data.api.dto.CamifeyeHoleDto
import com.done.weather.data.api.dto.CamifeyeSettingsDto


interface CamifeyeRepository {
    suspend fun getSettings(): Result<CamifeyeSettingsDto?>
    suspend fun  postSettings(payload: CamifeyeSettingsDto): Result<Unit>
    suspend fun putSettings(payload: CamifeyeSettingsDto): Result<Unit>
    suspend fun getHoles(): Result<List<CamifeyeHoleDto>>
    suspend fun postRound(firstSeen: Long, lastSeen: Long, groupCount: Int): Result<Unit>
}
