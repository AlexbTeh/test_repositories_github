package com.done.domain.usecase

import com.done.domain.models.Player
import com.done.domain.models.RoundMeta
import com.done.domain.models.Scorecard
import com.done.domain.repository.RoundsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class CreateRoundAndSyncUseCase @Inject constructor(
    private val repo: RoundsRepository
) {
    suspend operator fun invoke(meta: RoundMeta, players: List<Player>): Scorecard {
        val local = repo.createRoundLocal(meta, players)
        return try {
            repo.createRoundRemote(local.round.id)
        } catch (_: Throwable) {
            local
        }
    }
}
class ObserveScorecardUseCase @Inject constructor(
    private val repo: RoundsRepository
) {
    operator fun invoke(roundId: String): Flow<Scorecard> = repo.observeScorecard(roundId)
}
class SetStrokeUseCase @Inject constructor(
    private val repo: RoundsRepository
) {
    suspend operator fun invoke(roundId: String, playerId: String, hole: Int, strokes: Int?) =
        repo.setStroke(roundId, playerId, hole, strokes)
}
class AddPlayerLocalUseCase @Inject constructor(
    private val repo: RoundsRepository
) {
    suspend operator fun invoke(roundId: String, player: Player) =
        repo.addPlayerLocal(roundId, player)
}
class RemovePlayerLocalUseCase @Inject constructor(
    private val repo: RoundsRepository
) {
    suspend operator fun invoke(roundId: String, playerId: String) =
        repo.removePlayerLocal(roundId, playerId)
}
class SubmitRoundUseCase @Inject constructor(
    private val repo: RoundsRepository
) {
    suspend operator fun invoke(roundId: String): Boolean {
        return try {
            val ok = repo.postScoresRemote(roundId)
            if (!ok) repo.markSubmittedLocal(roundId)
            ok
        } catch (_: Throwable) {
            repo.markSubmittedLocal(roundId)
            false
        }
    }
}
