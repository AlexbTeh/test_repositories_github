package com.done.weather.data.datastore.preferences.entity

import androidx.annotation.FloatRange
import com.done.weather.data.datastore.preferences.AppPreferencesDefault
import com.done.weather.data.datastore.preferences.serializer.DurationIsoSerializer
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class GpsPreferencesEntity(
    @FloatRange(from = 0.0)
    val minAccuracy: Float = AppPreferencesDefault.GpsDefault.MIN_ACCURACY,
    @FloatRange(from = 0.0)
    val minDistanceChange: Float = AppPreferencesDefault.GpsDefault.MIN_DISTANCE_CHANGE, // In fact, this parameter affects the Engine logic, not the primary GPS processing.
    @FloatRange(from = 0.0)
    val maxSpeed: Float = AppPreferencesDefault.GpsDefault.MAX_SPEED,
    @Serializable(with = DurationIsoSerializer::class)
    val homebaseResetRoundTime: Duration = AppPreferencesDefault.GpsDefault.HOMEBASE_RESET_ROUND_TIME, // In fact, this parameter affects the Engine logic, not the primary GPS processing.
    @Serializable(with = DurationIsoSerializer::class)
    val locationUpdatePeriod: Duration = AppPreferencesDefault.GpsDefault.LOCATION_UPDATE_PERIOD
)