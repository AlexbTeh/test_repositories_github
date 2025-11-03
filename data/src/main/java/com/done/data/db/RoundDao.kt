package com.done.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RoundDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertRound(entity: RoundEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertPlayers(players: List<PlayerEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertScores(scores: List<ScoreEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertScore(score: ScoreEntity)

    @Query("SELECT * FROM rounds WHERE id = :id") fun observeRound(id: String): Flow<RoundEntity?>
    @Query("SELECT * FROM players WHERE roundId = :roundId") fun observePlayers(roundId: String): Flow<List<PlayerEntity>>
    @Query("SELECT * FROM scores WHERE roundId = :roundId") fun observeScores(roundId: String): Flow<List<ScoreEntity>>

    @Query("DELETE FROM players WHERE roundId = :roundId AND id = :playerId") suspend fun deletePlayer(roundId: String, playerId: String)
    @Query("UPDATE rounds SET isSubmitted = 1 WHERE id = :roundId") suspend fun markSubmitted(roundId: String)
}
