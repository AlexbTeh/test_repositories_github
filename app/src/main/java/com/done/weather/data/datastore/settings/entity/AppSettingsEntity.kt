package com.done.weather.data.datastore.settings.entity

import kotlinx.serialization.Serializable

@Serializable
data class AppSettingsEntity(
    val cartNumber: String = "Unknown",

    val facilityId: Long = -1L,
    val phones: List<PhoneEntity> = listOf(),
    val licences: List<LicenceEntity> = listOf(),
    val messagesTemplates: List<MessageTemplateEntity> = listOf(),
    val marshalMode: Boolean = false,

    val adVersion: Long? = null,
    val courseVersion: Long? = null,
    val geofenceVersion: Long? = null,
    val preferenceVersion: Long? = null,
)