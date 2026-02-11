package com.done.weather.core.network

import info.verifeye.vgps.core.network.NetworkState
import kotlinx.coroutines.flow.StateFlow

interface NetworkMonitor {
    companion object {
        const val MIN_SIGNAL_LEVEL = 0
        const val MAX_SIGNAL_LEVEL = 4
    }

    val networkState: StateFlow<NetworkState>
}
