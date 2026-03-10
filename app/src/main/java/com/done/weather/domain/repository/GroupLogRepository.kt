package com.done.weather.domain.repository

import com.done.weather.domain.model.camera_detect.GroupRecord

interface GroupLogRepository {
    fun load(): List<GroupRecord>
    fun append(record: GroupRecord)
    fun clear()
}
