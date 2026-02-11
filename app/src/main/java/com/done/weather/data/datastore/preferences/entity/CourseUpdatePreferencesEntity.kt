package com.done.weather.data.datastore.preferences.entity

import com.done.weather.data.datastore.preferences.AppPreferencesDefault
import com.done.weather.data.datastore.preferences.serializer.DurationIsoSerializer
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class CourseUpdatePreferencesEntity(
    @Serializable(with = DurationIsoSerializer::class)
    val courseUpdatePeriod: Duration = AppPreferencesDefault.CourseUpdateDefault.COURSE_UPDATE_PERIOD,
    @Serializable(with = DurationIsoSerializer::class)
    val backgroundUpdatePeriod: Duration = AppPreferencesDefault.CourseUpdateDefault.BACKGROUND_UPDATE_PERIOD
)
