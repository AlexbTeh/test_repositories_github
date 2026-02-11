package com.done.weather.data.datastore.preferences.entity

import kotlinx.serialization.Serializable

@Serializable
data class AppPreferencesEntity(
    val displayPreferences: DisplayPreferencesEntity = DisplayPreferencesEntity(),
    val demoPreferences: DemoPreferencesEntity = DemoPreferencesEntity(),
    val gpsPreferences: GpsPreferencesEntity = GpsPreferencesEntity(),
    val logPreferences: LogPreferencesEntity = LogPreferencesEntity(),
    val courseUpdatePreferences: CourseUpdatePreferencesEntity = CourseUpdatePreferencesEntity(),
    val otherCartsPreferences: OtherCartsPreferencesEntity = OtherCartsPreferencesEntity(),
    val networkPreferences: NetworkPreferencesEntity = NetworkPreferencesEntity(),
    val marshalModePreferences: MarshalModePreferencesEntity = MarshalModePreferencesEntity()
)
