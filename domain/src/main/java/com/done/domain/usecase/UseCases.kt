package com.done.domain.usecase

import com.done.domain.models.Player
import com.done.domain.models.RoundMeta
import com.done.domain.models.Scorecard
import com.done.domain.repository.RoundsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateRoundUseCase @Inject constructor(private val repo: RoundsRepository) {
    suspend operator fun invoke(meta: RoundMeta, players: List<Player>) = repo.createRound(meta, players)
}
class ObserveScorecardUseCase @Inject constructor(private val repo: RoundsRepository) {
    operator fun invoke(roundId: String): Flow<Scorecard> = repo.observeScorecard(roundId)
}
class SetStrokeUseCase @Inject constructor(private val repo: RoundsRepository) {
    suspend operator fun invoke(roundId: String, playerId: String, hole: Int, strokes: Int?) =
        repo.setStroke(roundId, playerId, hole, strokes)
}
class AddPlayerUseCase @Inject constructor(private val repo: RoundsRepository) {
    suspend operator fun invoke(roundId: String, player: Player) = repo.addPlayer(roundId, player)
}
class RemovePlayerUseCase @Inject constructor(private val repo: RoundsRepository) {
    suspend operator fun invoke(roundId: String, playerId: String) = repo.removePlayer(roundId, playerId)
}
class SubmitOfflineUseCase @Inject constructor(private val repo: RoundsRepository) {
    suspend operator fun invoke(roundId: String) = repo.submitOffline(roundId)
}
