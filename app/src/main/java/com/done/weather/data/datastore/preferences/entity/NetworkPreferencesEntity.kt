package com.done.weather.data.datastore.preferences.entity

import com.done.weather.data.datastore.preferences.AppPreferencesDefault
import kotlinx.serialization.Serializable

@Serializable
data class NetworkPreferencesEntity(
    val lowMobileDataConsumption: Boolean = AppPreferencesDefault.NetworkDefault.LOW_MOBILE_DATA_CONSUMPTION,
)