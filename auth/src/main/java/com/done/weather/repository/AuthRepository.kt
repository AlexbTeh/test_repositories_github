package com.done.weather.repository

interface AuthRepository {
    /**
     * Try to authorize device
     * @return token
     */
    suspend fun authenticate(deviceId: String): Result<String>
}