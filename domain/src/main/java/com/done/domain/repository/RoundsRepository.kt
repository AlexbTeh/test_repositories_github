package com.done.domain.repository

import com.done.domain.models.*
import kotlinx.coroutines.flow.Flow

interface RoundsRepository {
    suspend fun createRoundLocal(meta: RoundMeta, players: List<Player>): Scorecard
    fun observeScorecard(roundId: String): Flow<Scorecard>
    suspend fun setStroke(roundId: String, playerId: String, holeIndex: Int, strokes: Int?)
    suspend fun addPlayerLocal(roundId: String, player: Player)
    suspend fun removePlayerLocal(roundId: String, playerId: String)
    suspend fun markSubmittedLocal(roundId: String)

    suspend fun createRoundRemote(roundId: String): Scorecard                  // POST /rounds
    suspend fun updateRoundRemote(roundId: String): Scorecard                  // PUT /rounds/{id}
    suspend fun postScoresRemote(roundId: String): Boolean                     // POST /rounds/{id}/submit
}

