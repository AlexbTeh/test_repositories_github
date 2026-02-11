package com.done.weather.domain.usecase

class TrackGroupUseCase(
    private val noPeopleThresholdMs: Long = 60_000L,
    private val cooldownMs: Long = 60_000L
) {

    sealed class Event {
        data class GroupClosed(
            val maxPlayers: Int,
            val firstSeen: Long,
            val lastSeen: Long
        ) : Event()
    }

    private var active = false
    private var groupFirstSeen: Long = 0L
    private var groupLastSeen: Long = 0L
    private var lastNonZeroAt: Long = 0L
    private var currentMaxPlayers: Int = 0
    private var cooldownUntil: Long = 0L

    fun currentMax(): Int = currentMaxPlayers
    fun isCooldown(now: Long): Boolean = now < cooldownUntil
    fun cooldownRemainingSeconds(now: Long): Int =
        ((cooldownUntil - now).coerceAtLeast(0L) / 1000L).toInt()

    fun onPeopleCount(peopleCount: Int, now: Long): Event? {
        if (isCooldown(now)) {
            // в cooldown не создаём группу
            resetActive()
            return null
        }

        if (peopleCount > 0) {
            if (!active) {
                active = true
                groupFirstSeen = now
                currentMaxPlayers = 0
            }
            groupLastSeen = now
            lastNonZeroAt = now
            if (peopleCount > currentMaxPlayers) currentMaxPlayers = peopleCount
            return null
        }

        // peopleCount == 0
        if (!active) return null

        if (now - lastNonZeroAt >= noPeopleThresholdMs) {
            val event = Event.GroupClosed(
                maxPlayers = currentMaxPlayers,
                firstSeen = groupFirstSeen,
                lastSeen = groupLastSeen
            )
            cooldownUntil = now + cooldownMs
            resetActive()
            return event
        }

        return null
    }

    private fun resetActive() {
        active = false
        groupFirstSeen = 0L
        groupLastSeen = 0L
        lastNonZeroAt = 0L
        currentMaxPlayers = 0
    }
}


