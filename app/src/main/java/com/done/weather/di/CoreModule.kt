package com.done.weather.di

import coil.ImageLoader
import coil.memory.MemoryCache
import coil.request.CachePolicy
import com.done.weather.core.network.NetworkMonitor
import com.done.weather.data.core.NetworkMonitorImpl
import com.done.weather.data.vision.PersonDetector
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private const val MEMORY_CACHE_BYTES: Int = 48 * 1024 * 1024   // ~32 MB

val coreModule = module {
    single<ImageLoader> {
        ImageLoader.Builder(androidApplication())
            .memoryCache(
                MemoryCache.Builder(androidApplication())
                    .maxSizeBytes(MEMORY_CACHE_BYTES)
                    .build()
            )
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.DISABLED)
            .networkCachePolicy(CachePolicy.DISABLED)
            .allowHardware(true)
            .crossfade(false)
            .build()
    }

    single<NetworkMonitor> {
        NetworkMonitorImpl(
            context = androidApplication()
        )
    }
    single {
        PersonDetector(
            appContext = androidContext()
        )
    }
}