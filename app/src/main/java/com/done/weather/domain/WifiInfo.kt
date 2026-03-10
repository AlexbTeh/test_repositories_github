package com.done.weather.domain

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import java.net.NetworkInterface
import java.util.Collections
import java.util.Locale

object WifiInfo {
    // mac address
    fun getMacAddress(context: Context): String {
        val manager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val info = manager.connectionInfo
        var retVal = ""
        if (info.macAddress != null) retVal = info.macAddress.uppercase(Locale.getDefault())

        try {
            val all: List<NetworkInterface> =
                Collections.list(NetworkInterface.getNetworkInterfaces())
            for (nif in all) {
                if (!nif.name.equals("wlan0", ignoreCase = true)) continue

                val macBytes = nif.hardwareAddress ?:      return "unknown"

                val res1 = StringBuilder()
                for (b in macBytes) {
                    res1.append(
                        String.format(
                            Locale.getDefault(),
                            "%02X",
                            b.toInt() and 0xFF
                        ) + ":"
                    )
                }

                // remove ":" in the end of string
                if (res1.isNotEmpty()) res1.deleteCharAt(res1.length - 1)

                return res1.toString().uppercase(Locale.getDefault())
            }
        } catch (ex: Exception) {
            //handle exception
            ex.printStackTrace()
        }

        return "unknown"
    }

    @JvmStatic
    fun getDeviceId(context: Context): String {
        var address = getMacAddress(context).uppercase(Locale.getDefault())
//        address = "90:09:17:11:62:86"
        //address = "00:08:22:f2:e4:fb";
        //address = "00:08:22:30:E3:FB";
        //address = "ec:a8:6b:a2:af:97";
//        address = "c8:d5:fe:fc:d5:b1";
//        address = "c8:d5:fe:fe:0f:db". // - small tablet;
        if (address == "UNKNOWN") address = "02:00:00:00:00:00"
//        address = "02:00:00:00:00:00"
        return address.replace(":".toRegex(), "")
    }

    @JvmStatic
    fun commonInfo(context: Context): String {
        val manager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val info = manager.connectionInfo

        return String.format(
            Locale.getDefault(),
            "%s,%s,%s",
            getMacAddress(context),
            info.ssid,
            WifiManager.calculateSignalLevel(info.rssi, 5)
        )
    }

    @JvmStatic
    fun currentNetworkType(context: Context): NETWORK_TYPE {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork ?: return NETWORK_TYPE.NONE
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
            ?: return NETWORK_TYPE.NONE

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NETWORK_TYPE.WIFI
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NETWORK_TYPE.SIM
            else -> NETWORK_TYPE.UNKNOWN
        }
    }

    /*
     * network connection
     */
    enum class NETWORK_TYPE {
        WIFI,
        SIM,
        UNKNOWN,
        NONE
    }
}
