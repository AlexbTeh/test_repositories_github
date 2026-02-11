package com.done.weather.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.done.weather.domain.repository.GroupLogRepository
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.done.weather.domain.model.camera_detect.GroupRecord

class GroupLogRepositoryImpl(
    private val prefs: SharedPreferences
) : GroupLogRepository {

    private val gson = Gson()
    private val key = "group_log"

    private val listType = object : TypeToken<List<GroupRecord>>() {}.type

    override fun load(): List<GroupRecord> {
        val json = prefs.getString(key, "[]") ?: "[]"
        return runCatching {
            gson.fromJson<List<GroupRecord>>(json, listType)
        }.getOrElse { emptyList() }
    }

    override fun append(record: GroupRecord) {
        val list = load().toMutableList()
        list.add(0, record)
        val json = gson.toJson(list)
        prefs.edit { putString(key, json) }
    }

    override fun clear() {
        prefs.edit { remove(key) }
    }
}
