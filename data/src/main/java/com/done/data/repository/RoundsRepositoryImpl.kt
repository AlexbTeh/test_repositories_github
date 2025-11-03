package com.done.data.repository

import com.done.data.db.AppDatabase
import com.done.data.db.ScoreEntity
import com.done.data.mapper.toDomain
import com.done.data.mapper.toEntity
import com.done.domain.models.Player
import com.done.domain.models.RoundMeta
import com.done.domain.models.Scorecard
import com.done.domain.repository.RoundsRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class RoundsRepositoryImpl @Inject constructor(
    private val db: AppDatabase
) : RoundsRepository {

    private val dao get() = db.roundDao()

    override suspend fun createRound(meta: RoundMeta, players: List<Player>): Scorecard {
        dao.upsertRound(meta.toEntity())
        dao.upsertPlayers(players.map { it.toEntity(meta.id) })

        val scores = players.flatMap { p ->
            (1..meta.holes).map { h -> ScoreEntity(meta.id, p.id, h, null) }
        }
        dao.upsertScores(scores)
        return Scorecard(meta, players, par = 4, scores = emptyMap())
    }

    override fun observeScorecard(roundId: String): Flow<Scorecard> {
        val roundF = dao.observeRound(roundId).filterNotNull().map { it.toDomain() }
        val playersF = dao.observePlayers(roundId).map { it.map { p -> p.toDomain() } }
        val scoresF = dao.observeScores(roundId).map { list ->
            buildMap {
                list.forEach { put(it.playerId to it.hole, it.strokes) }
            }
        }
        return combine(roundF, playersF, scoresF) { r, ps, sc ->
            Scorecard(round = r, players = ps, par = 4, scores = sc)
        }
    }

    override suspend fun setStroke(roundId: String, playerId: String, holeIndex: Int, strokes: Int?) {
        dao.upsertScore(ScoreEntity(roundId, playerId, holeIndex, strokes))
    }

    override suspend fun addPlayer(roundId: String, player: Player) {
        dao.upsertPlayers(listOf(player.toEntity(roundId)))
        val holes = dao.observeRound(roundId).first()?.holes ?: 18
        dao.upsertScores((1..holes).map { h -> ScoreEntity(roundId, player.id, h, null) })
    }

    override suspend fun removePlayer(roundId: String, playerId: String) {
        dao.deletePlayer(roundId, playerId)
    }

    override suspend fun submitOffline(roundId: String) {
        dao.markSubmitted(roundId)
    }
}
