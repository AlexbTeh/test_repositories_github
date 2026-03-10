package com.done.weather.utils

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import androidx.core.content.ContextCompat
import java.io.File

val Context.bluetoothAdapter: BluetoothAdapter?
    get() = ContextCompat.getSystemService(this, BluetoothManager::class.java)?.adapter

@SuppressLint("UnspecifiedRegisterReceiverFlag")
fun Context.registerExportedReceiver(receiver: BroadcastReceiver?, filter: IntentFilter) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
    } else {
        registerReceiver(receiver, filter)
    }
}

@SuppressLint("UnspecifiedRegisterReceiverFlag")
fun Context.registerNonExportedReceiver(receiver: BroadcastReceiver?, filter: IntentFilter) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
    } else {
        registerReceiver(receiver, filter)
    }
}

val Context.appDataDirectory: File
    get() {
        val appCacheDir = File(filesDir, "app_cache")
        if (!appCacheDir.exists()) {
            appCacheDir.mkdirs()
        }
        return appCacheDir
    }