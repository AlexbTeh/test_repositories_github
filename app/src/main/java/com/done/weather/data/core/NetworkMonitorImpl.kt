package com.done.weather.data.core

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
import info.verifeye.vgps.core.network.CellularNetworkType
import com.done.weather.core.network.NetworkMonitor
import info.verifeye.vgps.core.network.NetworkState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NetworkMonitorImpl(private val context: Context) : NetworkMonitor {
    private val connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val telephonyManager: TelephonyManager =
        context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    private val _networkState = MutableStateFlow<NetworkState>(NetworkState.None)
    override val networkState: StateFlow<NetworkState> = _networkState.asStateFlow()

    private val networkCallback = object : NetworkCallback() {

        override fun onAvailable(network: Network) {
            updateState(network)
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            updateState(network, networkCapabilities)
        }

        override fun onLost(network: Network) {
            if (connectivityManager.activeNetwork == null) {
                _networkState.value = NetworkState.None
            } else {
                updateState()
            }
        }

        override fun onUnavailable() {
            _networkState.value = NetworkState.None
        }
    }

    init {
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
        updateState()
    }

    private fun updateState(
        network: Network? = connectivityManager.activeNetwork,
        capabilitiesOverride: NetworkCapabilities? = null
    ) {
        val activeNetwork = network ?: run {
            _networkState.value = NetworkState.None
            return
        }

        val capabilities = capabilitiesOverride
            ?: connectivityManager.getNetworkCapabilities(activeNetwork)
            ?: run {
                _networkState.value = NetworkState.Unknown
                return
            }

        val signalStrengthDbm: Int =
            capabilities.signalStrength

        val newState: NetworkState = when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                NetworkState.Wifi(
                    signalLevel = mapSignalDbmToLevel(signalStrengthDbm)
                )
            }

            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_PHONE_STATE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    NetworkState.Cellular(
                        signalLevel = mapSignalDbmToLevel(signalStrengthDbm),
                        networkType = mapCellularType(TelephonyManager.NETWORK_TYPE_UNKNOWN)
                    )
                } else {
                    val rawNetworkType = telephonyManager.dataNetworkType
                    val mappedType = mapCellularType(rawNetworkType)

                    NetworkState.Cellular(
                        signalLevel = mapSignalDbmToLevel(signalStrengthDbm),
                        networkType = mappedType
                    )
                }
            }

            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> {
                NetworkState.Unknown
            }

            else -> NetworkState.None
        }

        _networkState.value = newState
    }

    private fun mapCellularType(telephonyNetworkType: Int): CellularNetworkType {
        return when (telephonyNetworkType) {
            // 2G
            TelephonyManager.NETWORK_TYPE_GPRS,
            TelephonyManager.NETWORK_TYPE_EDGE,
            TelephonyManager.NETWORK_TYPE_CDMA,
            TelephonyManager.NETWORK_TYPE_1xRTT,
            TelephonyManager.NETWORK_TYPE_IDEN -> CellularNetworkType.G2

            // 3G
            TelephonyManager.NETWORK_TYPE_UMTS,
            TelephonyManager.NETWORK_TYPE_EVDO_0,
            TelephonyManager.NETWORK_TYPE_EVDO_A,
            TelephonyManager.NETWORK_TYPE_HSDPA,
            TelephonyManager.NETWORK_TYPE_HSUPA,
            TelephonyManager.NETWORK_TYPE_HSPA,
            TelephonyManager.NETWORK_TYPE_EVDO_B,
            TelephonyManager.NETWORK_TYPE_EHRPD,
            TelephonyManager.NETWORK_TYPE_HSPAP -> CellularNetworkType.G3

            // 4G
            TelephonyManager.NETWORK_TYPE_LTE -> CellularNetworkType.G4

            // 5G
            TelephonyManager.NETWORK_TYPE_NR -> CellularNetworkType.G5

            else -> CellularNetworkType.UNKNOWN
        }
    }

    private fun mapSignalDbmToLevel(signalDbm: Int?): Int {
        if (signalDbm == null) {
            return NetworkMonitor.MIN_SIGNAL_LEVEL
        }

        val level = when {
            signalDbm >= -55 -> 4
            signalDbm >= -65 -> 3
            signalDbm >= -75 -> 2
            signalDbm >= -85 -> 1
            else -> 0
        }

        return level.coerceIn(NetworkMonitor.MIN_SIGNAL_LEVEL, NetworkMonitor.MAX_SIGNAL_LEVEL)
    }
}