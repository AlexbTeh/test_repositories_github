package com.done.weather.data.api.dto

import kotlinx.serialization.Serializable

/**
 * Settings payload (POST/PUT settings)
 */
@Serializable
data class CamifeyeSettingsDto(
    val name: String,
    val holeId: Int,
    /**
     * String coordinates: "lat,lon"
     * Example: "-27.65659640367253,153.1483833911028"
     */
    val location: String,
    val expectedGroupSize: Int
)

/**
 * GET list holes item
 * Based on your real response:
 * {
 *   "id":2557,
 *   "description":"Hole 1",
 *   "physicalCourse":{"id":297,"name":"Front 9"},
 *   "duration":15,
 *   "par":4,
 *   "tee":"-27...,153...",
 *   "fairway":"-27...,153...",
 *   "green":"-27...,153..."
 * }
 */
@Serializable
data class CamifeyeHoleDto(
    val id: Int,

    // server field is "description" (NOT "name")
    val description: String? = null,

    val physicalCourse: CamifeyePhysicalCourseDto? = null,

    val duration: Int? = null,
    val par: Int? = null,

    // coordinates as string "lat,lon"
    val tee: String? = null,
    val fairway: String? = null,
    val green: String? = null
)

@Serializable
data class CamifeyePhysicalCourseDto(
    val id: Int,
    val name: String? = null
)

/**
 * POST round payload
 */
@Serializable
data class CamifeyeRoundCreateDto(
    val firstSeen: String,   // ISO-8601 UTC
    val lastSeen: String,    // ISO-8601 UTC
    val groupCount: Int
)