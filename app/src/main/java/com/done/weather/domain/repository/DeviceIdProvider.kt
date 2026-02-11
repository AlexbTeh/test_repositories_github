package com.done.weather.domain.repository

interface DeviceIdProvider {
    /** MAC-адрес в формате AA:BB:CC:DD:EE:FF (или что вернёт система) */
    fun getDeviceId(): String
}
