package com.done.data.mapper

import com.done.data.models.*
import com.done.domain.models.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val DT_CREATE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.US)      // create: "yyyy-MM-dd HH:mm"
private val DT_RESP   = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US)   // response: "yyyy-MM-dd HH:mm:ss"

private fun String.ucFirst(): String = replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.US) else it.toString() }
private fun teeOut(c: TeeColour): String = c.name.lowercase(Locale.US).replace('_',' ')
private fun typeOut(t: RoundType): String = t.name.lowercase(Locale.US).replace('_',' ').ucFirst()

private fun teeIn(s: String): TeeColour =
    runCatching { TeeColour.valueOf(s.uppercase(Locale.US).replace(' ','_')) }.getOrElse { TeeColour.RED }

private fun typeIn(s: String): RoundType =
    runCatching { RoundType.valueOf(s.uppercase(Locale.US).replace(' ','_')) }.getOrElse { RoundType.STABLEFORD }


fun RoundMeta.toCreateRequest(players: List<Player>): CreateRoundRequest =
    CreateRoundRequest(
        tees = teeOut(tee),
        holesCount = holes,
        roundType = typeOut(type),
        players = players.map { PlayerRequest(playerName = it.name, memberId = it.memberId) },
        roundTime = startTime.format(DT_CREATE)
    )

fun RoundMeta.toUpdateRequest(players: List<Player>): UpdateRoundRequest =
    UpdateRoundRequest(
        tees = teeOut(tee),
        holesCount = holes,
        roundType = typeOut(type),
        players = players.map { PlayerRequest(playerName = it.name, memberId = it.memberId) }
    )

fun RoundResponse.toRoundMeta(prev: RoundMeta): RoundMeta {
    val start = LocalDateTime.parse(this.roundTime, DT_RESP)
    return prev.copy(
        remoteId   = this.roundId,
        startTime  = start,
        date       = LocalDate.parse(this.roundTime.substring(0, 10)), // "yyyy-MM-dd ..."
        tee        = teeIn(this.tees),
        holes      = this.holesCount,
        type       = typeIn(this.roundType)
    )
}

