package com.done.weather.data.datastore.settings.entity

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class LicenceEntity(
    val code: String,
    val expirationDate: LocalDate,
)