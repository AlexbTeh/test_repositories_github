package com.done.weather.domain.usecase

import coil.ImageLoader
import com.done.weather.domain.application.AppLifecycleManager

class RestartAppUseCase(
    private val appLifecycleManager: AppLifecycleManager,
    private val imageLoader: ImageLoader
) {
    suspend operator fun invoke() {
        imageLoader.memoryCache?.clear()
        appLifecycleManager.stopAllServices()
        appLifecycleManager.restartAppFromSplash()
    }
}