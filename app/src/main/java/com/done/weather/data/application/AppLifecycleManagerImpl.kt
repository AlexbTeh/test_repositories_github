package com.done.weather.data.application

import android.content.Context
import android.content.Intent
import com.done.weather.domain.application.AppLifecycleManager
import com.done.weather.ui.activity.SplashActivity
import timber.log.Timber

class AppLifecycleManagerImpl(
    private val context: Context,
) : AppLifecycleManager {

    companion object {
        private const val TAG = "AppLifecycleManager"
    }

    override suspend fun startInitialServices() {
        // ✅ Splash должен просто запуститься — ничего тяжёлого не стартуем
        Timber.tag(TAG).i("startInitialServices: skipped (Splash minimal)")
    }

    override suspend fun startMainServices() {
        // ✅ Splash minimal — ничего не запускаем
        Timber.tag(TAG).i("startMainServices: skipped (Splash minimal)")
    }

    override suspend fun startAllServices() {
        // если где-то вызывается startAllServices(), пусть не падает
        startInitialServices()
        startMainServices()
    }

    override suspend fun stopAllServices() {
        // ✅ нечего останавливать
        Timber.tag(TAG).i("stopAllServices: skipped (Splash minimal)")
    }

    override fun restartAppFromSplash() {
        context.startActivity(
            Intent(context, SplashActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
    }

    override fun restartAppFromSplashAndClearApp() {
        context.startActivity(
            Intent(context, SplashActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
    }
}
