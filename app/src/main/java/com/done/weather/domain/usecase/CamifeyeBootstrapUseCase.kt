package com.done.weather.domain.usecase

import com.done.weather.data.api.dto.CamifeyeSettingsDto
import com.done.weather.domain.repository.CamifeyeRepository

class CamifeyeBootstrapUseCase(
    private val repo: CamifeyeRepository
) {
    /**
     * По ТЗ:
     * 1) GET settings
     * 2) GET holes
     * 3) POST settings если нет, иначе PUT
     */
    suspend fun bootstrapAndUpsertSettings(
        cameraName: String,
        expectedGroupSize: Int,
        latitude: Double,
        longitude: Double
    ): Result<Unit> {

        val payload = CamifeyeSettingsDto(
            name = cameraName,
            holeId = 1,
            location = "$latitude,$longitude", // строка, как требует API
            expectedGroupSize = expectedGroupSize
        )

        val existing = repo.getSettings()

        val upsert = if (existing.isSuccess) {
            repo.putSettings(payload)
        } else {
            repo.postSettings(payload)
        }

        if (upsert.isFailure) return Result.failure(upsert.exceptionOrNull()!!)

        // holes по ТЗ
        val holes = repo.getHoles()
        if (holes.isFailure) return Result.failure(holes.exceptionOrNull()!!)

        return Result.success(Unit)
    }
}
