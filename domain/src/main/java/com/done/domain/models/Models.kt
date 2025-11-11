package com.done.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

enum class TeeColour { WHITE, BLACK, SILVER, RED, BURGUNDY, YELLOW, ORANGE, LIGHT_GREEN, DARK_GREEN, PURPLE, COPPER, GOLD, REFLEX_BLUE, DARK_BLUE }
enum class RoundType { STROKE, STABLEFORD }

@Parcelize
data class Player(
    val id: String = UUID.randomUUID().toString(), // локальный id (до ответа сервера)
    val name: String,
    val memberId: String? = null,                  // <- добавили для API
    val remoteId: Int? = null                      // <- id игрока на сервере (из ответа API)
) : Parcelable

data class RoundMeta(
    val id: String = UUID.randomUUID().toString(), // локальный id раунда
    val course: String,
    val date: LocalDate,
    val tee: TeeColour,
    val holes: Int,                                // 9/18/27/36
    val type: RoundType,
    val startTime: LocalDateTime = LocalDateTime.now(),
    val isSubmitted: Boolean = false,
    val remoteId: Int? = null                      // <- id раунда на сервере (из ответа API)
)

data class ScoreEntry(
    val playerId: String,  // local Player.id
    val holeIndex: Int,    // 1..holes
    val strokes: Int?
)

data class Scorecard(
    val round: RoundMeta,
    val players: List<Player>,
    val par: Int = 4,
    val scores: Map<Pair<String, Int>, Int?> // key=(localPlayerId,hole)
)
