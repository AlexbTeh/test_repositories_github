package com.done.data.db

import androidx.room.*

@Entity(tableName = "rounds")
data class RoundEntity(
    @PrimaryKey val id: String,
    val course: String,
    val date: String,
    val tee: String,
    val holes: Int,
    val type: String,
    val startTime: String,
    val isSubmitted: Boolean
)

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey val id: String,
    val roundId: String,
    val name: String
)

@Entity(
    tableName = "scores",
    primaryKeys = ["roundId", "playerId", "hole"]
)
data class ScoreEntity(
    val roundId: String,
    val playerId: String,
    val hole: Int,
    val strokes: Int? // nullable -> ячейка пустая
)
