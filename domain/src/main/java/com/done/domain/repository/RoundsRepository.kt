package com.done.domain.repository

import com.done.domain.models.Player
import com.done.domain.models.RoundMeta
import com.done.domain.models.Scorecard
import kotlinx.coroutines.flow.Flow

interface RoundsRepository {
    suspend fun createRound(meta: RoundMeta, players: List<Player>): Scorecard
    fun observeScorecard(roundId: String): Flow<Scorecard>
    suspend fun setStroke(roundId: String, playerId: String, holeIndex: Int, strokes: Int?)
    suspend fun addPlayer(roundId: String, player: Player)
    suspend fun removePlayer(roundId: String, playerId: String)
    suspend fun submitOffline(roundId: String)
}
