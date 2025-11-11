package com.done.data.repository

import androidx.room.withTransaction
import com.done.data.api.ScorecardApi
import com.done.data.db.AppDatabase
import com.done.data.db.ScoreEntity
import com.done.data.mapper.*
import com.done.data.mappers.toCreateRequest
import com.done.data.mappers.toPlayers
import com.done.data.mappers.toRoundMeta
import com.done.data.mappers.toUpdateRequest
import com.done.data.models.PostScoresRequest
import com.done.data.models.ScoreRequest
import com.done.data.models.SubmitHole
import com.done.data.models.SubmitPlayer
import com.done.data.models.SubmitRequest
import com.done.domain.models.*
import com.done.domain.repository.RoundsRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class RoundsRepositoryImpl @Inject constructor(
    private val db: AppDatabase,
    private val api: ScorecardApi
) : RoundsRepository {

    private val dao get() = db.roundDao()

    override suspend fun createRoundLocal(meta: RoundMeta, players: List<Player>): Scorecard {
        db.withTransaction {
            dao.upsertRound(meta.toEntity())
            dao.upsertPlayers(players.map { it.toEntity(meta.id) })
            val scores = players.flatMap { p ->
                (1..meta.holes).map { h -> ScoreEntity(meta.id, p.id, h, null) }
            }
            dao.upsertScores(scores)
        }
        return Scorecard(meta, players, par = 4, scores = emptyMap())
    }


    override fun observeScorecard(roundId: String): Flow<Scorecard> {
        val roundF = dao.observeRound(roundId).filterNotNull().map { it.toDomain() }
        val playersF = dao.observePlayers(roundId).map { list -> list.map { it.toDomain() } }
        val scoresF = dao.observeScores(roundId).map { list ->
            buildMap {
                list.forEach { put(it.playerId to it.hole, it.strokes) }
            }
        }
        return combine(roundF, playersF, scoresF) { r, ps, sc ->
            Scorecard(
                r,
                ps,
                par = 4,
                scores = sc
            )
        }
    }

    override suspend fun setStroke(
        roundId: String,
        playerId: String,
        holeIndex: Int,
        strokes: Int?
    ) {
        dao.upsertScore(ScoreEntity(roundId, playerId, holeIndex, strokes))
    }

    override suspend fun addPlayerLocal(roundId: String, player: Player) {
        dao.upsertPlayers(listOf(player.toEntity(roundId)))
        val holes = dao.observeRound(roundId).first()!!.holes
        dao.upsertScores((1..holes).map { h -> ScoreEntity(roundId, player.id, h, null) })
    }

    override suspend fun removePlayerLocal(roundId: String, playerId: String) {
        dao.deletePlayer(roundId, playerId)
    }

    override suspend fun markSubmittedLocal(roundId: String) {
        dao.markSubmitted(roundId)
    }

    override suspend fun createRoundRemote(roundId: String): Scorecard {
        val round   = dao.observeRound(roundId).first()!!.toDomain()
        val players = dao.getPlayers(roundId).map { it.toDomain() }

        // 1) вызываем API
        val resp = api.createRound(round.toCreateRequest(players))

        // 2) сохраняем атомарно: remoteId раунда + remoteId игроков
        db.withTransaction {
            // round.remoteId
            dao.setRoundRemoteId(roundId, resp.roundId)

            // players.remoteId (мэппим по имени, но сохраняем те же локальные id)
            val syncedPlayers: List<Player> = resp.toPlayers(players)
            dao.upsertPlayers(syncedPlayers.map { it.toEntity(roundId) })
        }

        // 3) возвращаем актуальную карточку
        return observeScorecard(roundId).first()
    }



    override suspend fun updateRoundRemote(roundId: String): Scorecard {
        val round    = dao.observeRound(roundId).first()!!.toDomain()
        val remoteId = round.remoteId ?: error("No remote roundId")
        val players  = dao.getPlayers(roundId).map { it.toDomain() }

        val resp = api.updateRound(remoteId, round.toUpdateRequest(players))

        db.withTransaction {
            val newMeta = resp.toRoundMeta(round)
            dao.upsertRound(newMeta.toEntity())

            // ещё раз синхронизируем игроков (если бек вернул их список)
            val synced = resp.toPlayers(players)
            dao.upsertPlayers(synced.map { it.toEntity(roundId) })
        }

        return observeScorecard(roundId).first()
    }

    override suspend fun postScoresRemote(roundId: String): Boolean {
        // серверный id раунда для /rounds/{id}/submit
        val remoteRoundId = dao.getRoundRemoteId(roundId) ?: return false

        // игроки с их remoteId из Room
        val players = dao.getPlayers(roundId)  // List<PlayerEntity>
        val idMap: Map<String, Int> = players
            .mapNotNull { e -> e.remoteId?.let { rid -> e.id to rid } }
            .toMap() // localUuid -> serverPlayerId

        // все очки из Room
        val scores = dao.observeScores(roundId).first()

        // группируем по игроку и формируем тело запроса
        val grouped: List<SubmitPlayer> = scores
            .filter { it.strokes != null }
            .groupBy { it.playerId } // local uuid
            .mapNotNull { (localPid, holes) ->
                val serverPid = idMap[localPid] ?: return@mapNotNull null
                SubmitPlayer(
                    playerId = serverPid,
                    holes = holes.sortedBy { it.hole }.map { s ->
                        SubmitHole(
                            hole = s.hole,
                            par  = 4,              // по ТЗ пока фиксируем par=4
                            score = s.strokes!!
                        )
                    }
                )
            }

        if (grouped.isEmpty()) return false

        val ok = api.postScores(remoteRoundId, SubmitRequest(scores = grouped)).success
        if (ok) dao.markSubmitted(roundId)
        return ok
    }


}

