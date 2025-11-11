package com.done.data.db

import com.done.domain.models.Player
import com.done.domain.models.RoundMeta
import com.done.domain.models.RoundType
import com.done.domain.models.TeeColour

fun RoundMeta.toEntity() = RoundEntity(
    id, course, date, tee.name, holes, type.name, startTime, isSubmitted, remoteId
)
fun RoundEntity.toDomain() = RoundMeta(
    id, course, date,
    TeeColour.valueOf(tee),
    holes,
    RoundType.valueOf(type),
    startTime,
    isSubmitted,
    remoteId
)

fun Player.toEntity(roundId: String) = PlayerEntity(roundId, id, name, memberId, remoteId)
fun PlayerEntity.toDomain() = Player(id = id, name = name, memberId = memberId, remoteId = remoteId)
