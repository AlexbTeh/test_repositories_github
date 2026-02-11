package com.done.weather

import android.app.Application
import com.done.weather.di.coreModule
import com.done.weather.di.datastoreModule
import com.done.weather.di.deviceModule
import com.done.weather.di.networkModule
import com.done.weather.di.otherModule
import com.done.weather.di.repoModule
import com.done.weather.di.useCaseModule
import com.done.weather.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@App)
            modules(
                authModule,
                datastoreModule,
                networkModule,
                coreModule,
                repoModule,
                otherModule,
                useCaseModule,
                viewModelModule,
                deviceModule
            )
        }
    }
}