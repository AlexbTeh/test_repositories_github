package com.done.weather.data.repository

import com.done.weather.data.api.CamifeyeRoundsApi
import com.done.weather.data.api.CamifeyeSettingsApi
import info.verifeye.vgps.data.api.CamifeyeHoleApi
import com.done.weather.data.api.dto.CamifeyeHoleDto
import com.done.weather.data.api.dto.CamifeyeRoundCreateDto
import com.done.weather.data.api.dto.CamifeyeSettingsDto
import com.done.weather.domain.repository.CamifeyeRepository
import kotlinx.datetime.Instant

class CamifeyeRepositoryImpl(
    private val settingsApi: CamifeyeSettingsApi,
    private val holeApi: CamifeyeHoleApi,
    private val roundsApi: CamifeyeRoundsApi
) : CamifeyeRepository {

    override suspend fun getSettings(): Result<CamifeyeSettingsDto?> =
        settingsApi.getSettings()

    override suspend fun postSettings(payload: CamifeyeSettingsDto): Result<Unit> =
        settingsApi.postSettings(payload)

    override suspend fun putSettings(payload: CamifeyeSettingsDto): Result<Unit> =
        settingsApi.putSettings(payload)

    override suspend fun getHoles(): Result<List<CamifeyeHoleDto>> =
        holeApi.getHoles()

    override suspend fun postRound(firstSeen: Long, lastSeen: Long, groupCount: Int): Result<Unit> =
        roundsApi.postRound(
            CamifeyeRoundCreateDto(
                firstSeen = epochMsToIsoUtcNoMillis(firstSeen),
                lastSeen = epochMsToIsoUtcNoMillis(lastSeen),
                groupCount = groupCount
            )
        )
}
fun epochMsToIsoUtcNoMillis(ms: Long): String {
    val iso = Instant.fromEpochMilliseconds(ms).toString()
    // "2026-01-20T17:48:10.123Z" -> "2026-01-20T17:48:10Z"
    return iso.replace(Regex("\\.\\d{1,9}Z$"), "Z")
}
