package com.done.weather.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PreferencesDto(
    @SerialName("server_address") var serverAddress: String = "",
    @SerialName("setting_password") val settingPassword: String = "9799",
    @SerialName("setting_advanced_password") val settingAdvancedPassword: String = "8099",
    @SerialName("fontsize") val fontsize: String = "normal",

    @SerialName("marshal_email") val marshalEmail: String = "",
    @SerialName("marshal_password") val marshalPassword: String = "",
    @SerialName("marshal_url") val marshalUrl: String = ""
)