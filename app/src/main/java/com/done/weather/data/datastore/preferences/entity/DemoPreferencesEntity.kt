package com.done.weather.data.datastore.preferences.entity

import com.done.weather.data.datastore.preferences.AppPreferencesDefault
import kotlinx.serialization.Serializable

@Serializable
data class DemoPreferencesEntity(
    val enabled: Boolean = AppPreferencesDefault.DemoDefault.ENABLED,
    val standAtFirstDemoPoint: Boolean = AppPreferencesDefault.DemoDefault.STAND_AT_FIRST_DEMO_POINT,
    val showPath: Boolean = AppPreferencesDefault.DemoDefault.SHOW_PATH,
    val speed: Float = AppPreferencesDefault.DemoDefault.SPEED,
    val timeScale: Float = AppPreferencesDefault.DemoDefault.TIME_SCALE,
    val sendLocation: Boolean = AppPreferencesDefault.DemoDefault.SEND_LOCATION
)
