package com.done.weather

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.StringRes

class AppPreferences(private val context: Context) {

    private val instance: SharedPreferences =
        context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)


    operator fun contains(key: String): Boolean {
        return instance.contains(key)
    }

    fun getBool(@StringRes keyRes: Int, def: Boolean): Boolean {
        return instance.getBoolean(context.getString(keyRes), def)
    }

    fun getBool(key: String, def: Boolean): Boolean {
        return instance.getBoolean(key, def)
    }

    fun setBool(key: String, value: Boolean) {
        instance.edit()?.apply {
            putBoolean(key, value)
        }?.apply()
    }

    fun getInt(@StringRes keyRes: Int, @StringRes defRes: Int): Int {
        return instance.getInt(context.getString(keyRes), context.getString(defRes).toInt())
    }

    fun getInt(key: String, def: Int): Int {
        return instance.getInt(key, def)
    }

    fun setInt(key: String, value: Int) {
        instance.edit()?.apply {
            putInt(key, value)
        }?.apply()
    }

    fun getLong(key: String, def: Long): Long {
        return instance.getLong(key, def)
    }

    fun setLong(key: String, value: Long) {
        instance.edit()?.apply {
            putLong(key, value)
        }?.apply()
    }

    fun getStr(key: String, def: String): String {
        return instance.getString(key, def) ?: def
    }

    fun getStr(@StringRes keyRes: Int, def: String): String {
        return instance.getString(context.getString(keyRes), def) ?: def
    }

    fun getStr(@StringRes keyRes: Int, @StringRes defRes: Int): String {
        return instance.getString(context.getString(keyRes), context.getString(defRes))
            ?: context.getString(defRes)
    }

    fun setStr(key: String, value: String) {
        instance.edit()?.apply {
            putString(key, value)
        }?.apply()
    }

    fun getAllKeysWithPrefix(prefix: String): Set<String> {
        return instance.all.keys.filterTo(mutableSetOf()) { it.startsWith(prefix) }
    }

    fun removeKey(key: String) {
        instance.edit()?.apply {
            remove(key)
        }?.apply()
    }

    fun clear() {
        instance.edit()?.apply {
            clear()
        }?.apply()
    }

    object KEY {
        const val DOWNLOADED_COURSE_PACKAGE = "downloaded_course_package"
        const val COURSE_ROOT = "course_root"
        const val CLEARED_GPS_TESTING = "cleared_gps_testing"
        const val LAST_DATA_USAGE = "last_data_usage"

        const val TEMPERATURE_FOR_DIMMING_KEY = "temperature_for_dimming_key"
        const val INACTIVE_PERIOD_FOR_DIMMING_KEY = "inactive_period_for_dimming_key"
        const val GEOFENCE_CAMERA_ENABLED = "geofence_camera_enabled"
    }

    object Defaults {
        const val TEMPERATURE_FOR_DIMMING = 40
        const val INACTIVE_PERIOD_FOR_DIMMING = 30
    }
}
