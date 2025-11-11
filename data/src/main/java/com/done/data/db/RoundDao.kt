package com.done.data.db

import androidx.room.*
@Dao
interface RoundDao {
    // Rounds
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertRound(e: RoundEntity)
    @Query("SELECT * FROM rounds WHERE id=:id") fun observeRound(id: String): kotlinx.coroutines.flow.Flow<RoundEntity?>
    @Query("UPDATE rounds SET remoteId=:remoteId WHERE id=:localId") suspend fun setRoundRemoteId(localId: String, remoteId: Int)

    // Players
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertPlayers(list: List<PlayerEntity>)
    @Query("SELECT * FROM players WHERE roundId=:roundId") fun observePlayers(roundId: String): kotlinx.coroutines.flow.Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players WHERE roundId=:roundId ORDER BY name")
    suspend fun getPlayers(roundId: String): List<PlayerEntity>
    @Query("DELETE FROM players WHERE roundId=:roundId AND id=:playerId") suspend fun deletePlayer(roundId: String, playerId: String)
    @Query("UPDATE players SET remoteId=:remoteId WHERE roundId=:roundId AND id=:localId") suspend fun setPlayerRemoteId(roundId: String, localId: String, remoteId: Int)

    // Scores
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertScores(list: List<ScoreEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertScore(e: ScoreEntity)
    @Query("SELECT * FROM scores WHERE roundId=:roundId") fun observeScores(roundId: String): kotlinx.coroutines.flow.Flow<List<ScoreEntity>>

    // Submit flag
    @Query("UPDATE rounds SET isSubmitted=1 WHERE id=:roundId") suspend fun markSubmitted(roundId: String)
}

