package info.verifeye.vgps.core.network

sealed interface NetworkState {
    data object None : NetworkState
    data object Unknown : NetworkState
    data class Wifi(val signalLevel: Int) : NetworkState
    data class Cellular(val signalLevel: Int, val networkType: CellularNetworkType) : NetworkState
}

enum class CellularNetworkType {
    G2,
    G3,
    G4,
    G5,
    UNKNOWN
}