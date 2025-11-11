package com.done.data.models

data class PlayerRequest(
    val playerName: String,
    val memberId: String? = null
)

data class CreateRoundRequest(
    val tees: String,        // "Red", "Dark Green"
    val holesCount: Int,
    val roundType: String,   // "Stableford" / "Stroke"
    val players: List<PlayerRequest>,
    val roundTime: String
)

data class UpdateRoundRequest(
    val tees: String,
    val holesCount: Int,
    val roundType: String,
    val players: List<PlayerRequest>
)

data class RoundPlayerResponse(
    val playerId: Int,
    val playerName: String,
    val memberId: String?
)

data class RoundResponse(
    val roundId: Int,
    val roundTime: String, // "yyyy-MM-dd HH:mm:ss"
    val tees: String,
    val holesCount: Int,
    val roundType: String,
    val players: List<RoundPlayerResponse>
)

data class ScoreRequest(val playerId: Int, val hole: Int, val strokes: Int)
data class PostScoresRequest(val scores: List<ScoreRequest>)
data class SimpleResponse(val success: Boolean)


data class SubmitHole(
    val hole: Int,
    val par: Int,
    val score: Int
)

data class SubmitPlayer(
    val playerId: Int,
    val holes: List<SubmitHole>
)

data class SubmitRequest(
    val scores: List<SubmitPlayer>
)

