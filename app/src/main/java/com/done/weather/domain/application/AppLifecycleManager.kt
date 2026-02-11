package com.done.weather.domain.application

interface AppLifecycleManager {
    suspend fun startInitialServices()
    suspend fun startMainServices()

    suspend fun startAllServices()
    suspend fun stopAllServices()

    fun restartAppFromSplash()
    fun restartAppFromSplashAndClearApp()
}