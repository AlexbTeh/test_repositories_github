package com.done.weather.domain.usecase

import com.done.weather.domain.repository.CamifeyeRepository

class SendRoundUseCase(
    private val repo: CamifeyeRepository
) {
    suspend fun send(firstSeen: Long, lastSeen: Long, groupCount: Int): Result<Unit> {
        return repo.postRound(firstSeen, lastSeen, groupCount)
    }
}
