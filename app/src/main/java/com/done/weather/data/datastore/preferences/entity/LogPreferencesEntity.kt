package com.done.weather.data.datastore.preferences.entity

import androidx.annotation.FloatRange
import com.done.weather.data.datastore.preferences.AppPreferencesDefault
import com.done.weather.data.datastore.preferences.serializer.DurationIsoSerializer
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class LogPreferencesEntity(
    @Serializable(with = DurationIsoSerializer::class)
    val deviceSyncPeriod: Duration = AppPreferencesDefault.LogDefault.DEVICE_SYNC_PERIOD,
    @Serializable(with = DurationIsoSerializer::class)
    val locationUploadPeriodSim: Duration = AppPreferencesDefault.LogDefault.LOCATION_UPLOAD_PERIOD_SIM,
    @Serializable(with = DurationIsoSerializer::class)
    val locationUploadPeriodWifi: Duration = AppPreferencesDefault.LogDefault.LOCATION_UPLOAD_PERIOD_WIFI,
    @Serializable(with = DurationIsoSerializer::class)
    val locationReportingPeriod: Duration = AppPreferencesDefault.LogDefault.LOCATION_REPORTING_PERIOD,
    val locationReportingBatchSize: Int = AppPreferencesDefault.LogDefault.LOCATION_REPORTING_BATCH_SIZE,
    @FloatRange(from = 0.0)
    val minSpeed: Float = AppPreferencesDefault.LogDefault.MIN_SPEED,
    @FloatRange(from = 0.0)
    val maxSpeed: Float = AppPreferencesDefault.LogDefault.MAX_SPEED
)
