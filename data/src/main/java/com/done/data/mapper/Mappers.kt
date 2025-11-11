package com.done.data.mapper

import com.done.data.db.PlayerEntity
import com.done.data.db.RoundEntity
import com.done.domain.models.Player
import com.done.domain.models.RoundMeta
import com.done.domain.models.RoundType
import com.done.domain.models.TeeColour
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val D = DateTimeFormatter.ISO_LOCAL_DATE
private val DT = DateTimeFormatter.ISO_LOCAL_DATE_TIME

// data/mapper/RoomMappers.kt

fun RoundMeta.toEntity() = RoundEntity(
    id = id,
    course = course,
    date = date,
    tee = tee.name,
    holes = holes,
    type = type.name,
    startTime = startTime,
    isSubmitted = isSubmitted,
    remoteId = remoteId
)


fun RoundEntity.toDomain() = RoundMeta(
    id = id,
    course = course,
    date = date,
    tee = TeeColour.valueOf(tee),
    holes = holes,
    type = RoundType.valueOf(type),
    startTime = startTime,
    isSubmitted = isSubmitted,
    remoteId = remoteId
)


fun Player.toEntity(roundId: String) = PlayerEntity(roundId = roundId, id = id, name, memberId)
// data/mapper/RoomMappers.kt
fun PlayerEntity.toDomain() = Player(
    id = id,
    name = name,
    memberId = memberId,
    remoteId = remoteId
)

