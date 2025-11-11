package com.done.data.db

import androidx.room.TypeConverter
import com.done.domain.models.RoundType
import com.done.domain.models.TeeColour
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val DT: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

class DbConverters {

    // --- LocalDate
    @TypeConverter
    fun localDateToString(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    fun stringToLocalDate(value: String?): LocalDate? = value?.let(LocalDate::parse)

    // --- LocalDateTime
    @TypeConverter
    fun localDateTimeToString(value: LocalDateTime?): String? = value?.format(DT)

    @TypeConverter
    fun stringToLocalDateTime(value: String?): LocalDateTime? =
        value?.let { LocalDateTime.parse(it, DT) }

    // --- TeeColour enum
    @TypeConverter
    fun teeToString(value: TeeColour?): String? = value?.name

    @TypeConverter
    fun stringToTee(value: String?): TeeColour? = value?.let { TeeColour.valueOf(it) }

    // --- RoundType enum
    @TypeConverter
    fun roundTypeToString(value: RoundType?): String? = value?.name

    @TypeConverter
    fun stringToRoundType(value: String?): RoundType? = value?.let { RoundType.valueOf(it) }
}
