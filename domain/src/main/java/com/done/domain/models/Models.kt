package com.done.domain.models

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

enum class TeeColour { WHITE, BLACK, SILVER, RED, BURGUNDY, YELLOW, ORANGE, LIGHT_GREEN, DARK_GREEN, PURPLE, COPPER, GOLD, REFLEX_BLUE, DARK_BLUE }
enum class RoundType { STROKE, STABLEFORD }

data class Player(
    val id: String = UUID.randomUUID().toString(),
    val name: String
)

data class RoundMeta(
    val id: String = UUID.randomUUID().toString(),
    val course: String,
    val date: LocalDate,
    val tee: TeeColour,
    val holes: Int,                  // 9/18/27/36
    val type: RoundType,
    val startTime: LocalDateTime = LocalDateTime.now(),
    val isSubmitted: Boolean = false // офлайн сохранение = false
)

data class ScoreEntry(
    val playerId: String,
    val holeIndex: Int,              // 1..holes
    val strokes: Int?
)

data class Scorecard(
    val round: RoundMeta,
    val players: List<Player>,
    val par: Int = 4,                // по ТЗ фиксировано, из БД в будущем
    val scores: Map<Pair<String, Int>, Int?> // key=(playerId,hole)
)
