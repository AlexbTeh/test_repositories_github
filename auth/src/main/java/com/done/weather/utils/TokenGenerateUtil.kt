package com.done.weather.utils

import com.done.weather.BuildConfig
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.todayIn
import java.security.MessageDigest


internal object TokenGenerateUtil {
    @OptIn(ExperimentalStdlibApi::class)
    fun generateFirstToken(): String {
        val startDate = LocalDate.parse("2021-01-01")
        val currentDateUtc = Clock.System.todayIn(TimeZone.UTC)
        val daysBetweenDates = startDate.daysUntil(currentDateUtc)

        val initWord = "${2 * daysBetweenDates}${BuildConfig.SECRET_PHRASE}"
        val md = MessageDigest.getInstance("SHA-256")

        return md.digest(initWord.toByteArray()).toHexString()
    }
}
