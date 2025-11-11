package com.done.data.mappers

import com.done.data.models.CreateRoundRequest
import com.done.data.models.PlayerRequest
import com.done.data.models.RoundResponse
import com.done.data.models.UpdateRoundRequest
import com.done.domain.models.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

private fun String.toEnumKey() = trim().uppercase().replace(' ', '_').replace('-', '_')
private fun apiCase(name: String) =
    name.lowercase().replace('_',' ').split(' ').joinToString(" ") { it.replaceFirstChar(Char::titlecase) }

fun RoundResponse.toRoundMeta(old: RoundMeta?): RoundMeta =
    RoundMeta(
        id = old?.id ?: java.util.UUID.randomUUID().toString(),
        course = old?.course ?: "Cronulla Golf club",
        date = LocalDate.parse(roundTime.substring(0, 10)),
        startTime = LocalDateTime.parse(roundTime, dtf),
        tee = runCatching { TeeColour.valueOf(tees.toEnumKey()) }.getOrNull() ?: (old?.tee ?: TeeColour.RED),
        holes = holesCount,
        type = runCatching { RoundType.valueOf(roundType.toEnumKey()) }.getOrNull() ?: (old?.type ?: RoundType.STABLEFORD),
        isSubmitted = old?.isSubmitted ?: false,
        remoteId = roundId
    )

fun RoundResponse.toPlayers(localPlayers: List<Player>): List<Player> {
    val byName = localPlayers.associateBy { it.name }
    return players.map {
        val local = byName[it.playerName]
        Player(
            id = local?.id ?: java.util.UUID.randomUUID().toString(),
            name = it.playerName,
            memberId = it.memberId,
            remoteId = it.playerId
        )
    }
}

fun RoundMeta.toCreateRequest(players: List<Player>): CreateRoundRequest {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    return CreateRoundRequest(
        roundTime = startTime.format(formatter),
        tees = tee.name.lowercase(),
        holesCount = holes,
        roundType = type.name.replace("_", " "),
        players = players.map { PlayerRequest(playerName = it.name, memberId = it.memberId) }
    )
}

fun RoundMeta.toUpdateRequest(players: List<Player>) = UpdateRoundRequest(
    tees = apiCase(tee.name),
    holesCount = holes,
    roundType = apiCase(type.name),
    players = players.map { PlayerRequest(playerName = it.name, memberId = it.memberId) }
)



