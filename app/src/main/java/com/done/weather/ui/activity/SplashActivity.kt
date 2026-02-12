package com.done.weather.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.done.weather.domain.WifiInfo
import com.done.weather.domain.repository.BaseSettingsRepository
import com.done.weather.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.koin.android.ext.android.inject
import timber.log.Timber

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val authRepository: AuthRepository by inject()
    private val settings: BaseSettingsRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val existing = settings.bearerToken.first().trim()
            if (existing.isBlank()) {
                val deviceId = WifiInfo.getDeviceId(this@SplashActivity)
                val token = authRepository.authenticate(deviceId).getOrThrow()
                settings.updateBearerToken(token)
            }
            startActivity(Intent(this@SplashActivity, CameraDetectionActivity::class.java))
            finish()
        }
    }
}

