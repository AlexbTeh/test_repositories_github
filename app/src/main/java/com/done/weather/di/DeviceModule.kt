package com.done.weather.di


import com.done.weather.data.repository.AndroidDeviceIdProvider
import com.done.weather.domain.repository.DeviceIdProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val deviceModule = module {
    single<DeviceIdProvider> { AndroidDeviceIdProvider(androidContext()) }
}
