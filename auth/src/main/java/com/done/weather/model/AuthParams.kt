package com.done.weather.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthParams(
    val ciphertext: String = "",
    val encodedIv: String = "",
    val encodedAad: String = "",
    val encodedTag: String = ""
)
