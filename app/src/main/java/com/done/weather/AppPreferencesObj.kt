package com.done.weather


import android.content.Context
import android.content.SharedPreferences

@Deprecated("Use AppPreferences instead")
object AppPreferencesObj {

    var instance: SharedPreferences? = null
        internal set

    @JvmStatic
    fun initialize(pref: SharedPreferences?) {
        instance = pref
    }

    operator fun contains(key: String): Boolean {
        return instance?.contains(key) ?: false
    }

    @JvmStatic
    fun getBool(key: String, def: Boolean): Boolean {
        return instance?.getBoolean(key, def) ?: def
    }

    @JvmStatic
    fun setBool(key: String, value: Boolean) {
        instance?.edit()?.apply {
            putBoolean(key, value)
        }?.apply()
    }

    // int
    @JvmStatic
    fun getInt(key: String, def: Int): Int {
        return instance?.getInt(key, def) ?: def
    }

    @JvmStatic
    fun setInt(key: String, value: Int) {
        instance?.edit()?.apply {
            putInt(key, value)
        }?.apply()
    }

    // long
    @JvmStatic
    fun getLong(key: String, def: Long): Long {
        return instance?.getLong(key, def) ?: def
    }

    @JvmStatic
    fun setLong(key: String, value: Long) {
        instance?.edit()?.apply {
            putLong(key, value)
        }?.apply()
    }

    @JvmStatic
    fun getStr(key: String, def: String): String {
        return instance?.getString(key, def) ?: def
    }

    @JvmStatic
    fun setStr(key: String, value: String) {
        instance?.edit()?.apply {
            putString(key, value)
        }?.apply()
    }

    @JvmStatic
    fun removeKey(key: String) {
        instance?.edit()?.apply {
            remove(key)
        }?.apply()
    }

    @JvmStatic
    fun clear(context: Context) {
        instance?.edit()?.apply {
            clear()
        }?.apply()
    }

}

