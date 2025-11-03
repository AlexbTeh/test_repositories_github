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

fun RoundMeta.toEntity() = RoundEntity(
    id, course,
    date.format(D),
    tee.name, holes,
    type.name,
    startTime.format(DT),
    isSubmitted
)

fun RoundEntity.toDomain() = RoundMeta(
    id = id,
    course = course,
    date = LocalDate.parse(date, D),
    tee = TeeColour.valueOf(tee),
    holes = holes,
    type = RoundType.valueOf(type),
    startTime = LocalDateTime.parse(startTime, DT),
    isSubmitted = isSubmitted
)

fun Player.toEntity(roundId: String) = PlayerEntity(id, roundId, name)
fun PlayerEntity.toDomain() = Player(id = id, name = name)
