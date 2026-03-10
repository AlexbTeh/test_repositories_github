package com.done.weather.data.api.dto

import kotlinx.serialization.Serializable

/**
 * BaseResponse
 */
@Serializable
data class BaseResponseDto(
    /**
     * Additional information about the response
     */
    val message: String? = null,

    /**
     * Indicates if the request was successful
     */
    val success: Boolean?
)