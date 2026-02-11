package com.done.weather.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager
import android.provider.Settings
import com.done.weather.domain.repository.DeviceIdProvider
import java.util.Locale

class AndroidDeviceIdProvider(
    private val appContext: Context
) : DeviceIdProvider {

    override fun getDeviceId(): String {
        // 1) Пробуем Wi-Fi MAC (на новых Android может быть "02:00:00:00:00:00" или недоступен)
        val wifiMac = tryGetWifiMac()
        if (!wifiMac.isNullOrBlank() && wifiMac != "02:00:00:00:00:00") {
            return wifiMac.uppercase(Locale.US)
        }

        // 2) Фоллбек: ANDROID_ID (не MAC, но стабильный идентификатор)
        val androidId =
            Settings.Secure.getString(appContext.contentResolver, Settings.Secure.ANDROID_ID)
        if (!androidId.isNullOrBlank()) return androidId

        // 3) Последний фоллбек
        return "UNKNOWN_DEVICE"
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    private fun tryGetWifiMac(): String? {
        return try {
            val wifi =
                appContext.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            // Deprecated на новых API, но пробуем (на старых девайсах часто работает)
            val mac = wifi.connectionInfo?.macAddress
            mac
        } catch (_: Throwable) {
            null
        }
    }
}
