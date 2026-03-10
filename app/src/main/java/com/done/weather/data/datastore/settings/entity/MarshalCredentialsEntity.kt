package com.done.weather.data.datastore.settings.entity

import kotlinx.serialization.Serializable

@Serializable
data class MarshalCredentialsEntity(
    val login: String,
    val password: String,
)
