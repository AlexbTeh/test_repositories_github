package com.done.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "rounds")
data class RoundEntity(
    @PrimaryKey val id: String, // локальный
    val course: String,
    val date: LocalDate,
    val tee: String,
    val holes: Int,
    val type: String,
    val startTime: LocalDateTime,
    val isSubmitted: Boolean,
    val remoteId: Int? = null   // <- добавили
)

@Entity(
    tableName = "players",
    primaryKeys = ["roundId","id"]
)
data class PlayerEntity(
    val roundId: String,
    val id: String,            // локальный id
    val name: String,
    val memberId: String? = null, // <- добавили
    val remoteId: Int? = null     // <- добавили
)

@Entity(
    tableName = "scores",
    primaryKeys = ["roundId","playerId","hole"]
)
data class ScoreEntity(
    val roundId: String,
    val playerId: String, // local Player.id
    val hole: Int,
    val strokes: Int?
)
