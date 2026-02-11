package com.done.weather.data.datastore.preferences.entity

import kotlinx.serialization.Serializable

@Serializable
data class MarshalModePreferencesEntity(
    val url: String = "",
    val login: String = "",
    val password: String = "",
    val marshalModeType: MarshalModeTypeEntity = MarshalModeTypeEntity.SERVER_CONFIG
)
