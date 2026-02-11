package com.done.weather.data.datastore.preferences.entity

import com.done.weather.data.datastore.preferences.AppPreferencesDefault
import com.done.weather.data.datastore.preferences.serializer.DurationIsoSerializer
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class OtherCartsPreferencesEntity(
    val enabled: Boolean = AppPreferencesDefault.OtherCartsDefault.ENABLED,
    @Serializable(with = DurationIsoSerializer::class)
    val updatePeriod: Duration = AppPreferencesDefault.OtherCartsDefault.UPDATE_PERIOD
)