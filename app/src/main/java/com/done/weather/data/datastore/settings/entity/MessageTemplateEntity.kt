package com.done.weather.data.datastore.settings.entity

import kotlinx.serialization.Serializable

@Serializable
data class MessageTemplateEntity(
    val text: String
)