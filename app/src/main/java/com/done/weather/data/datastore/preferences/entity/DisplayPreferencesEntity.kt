package com.done.weather.data.datastore.preferences.entity

import com.done.weather.data.datastore.preferences.AppPreferencesDefault
import kotlinx.serialization.Serializable

@Serializable
data class DisplayPreferencesEntity(
    val showHoleFrontDistance: Boolean = AppPreferencesDefault.DisplayDefault.SHOW_HOLE_FRONT_DISTANCE,
    val showHoleBackDistance: Boolean = AppPreferencesDefault.DisplayDefault.SHOW_HOLE_BACK_DISTANCE,
    val allowSwitchUnits: Boolean = AppPreferencesDefault.DisplayDefault.ALLOW_SWITCH_UNITS,
    val useMilitaryTimeFormat: Boolean = AppPreferencesDefault.DisplayDefault.USE_MILITARY_TIME_FORMAT,
    val showHoleTimer: Boolean = AppPreferencesDefault.DisplayDefault.SHOW_HOLE_TIMER,
    val showConnectionIcons: Boolean = AppPreferencesDefault.DisplayDefault.SHOW_CONNECTION_ICONS,
    val useMessagesTemplatesOnly: Boolean = AppPreferencesDefault.DisplayDefault.USE_MESSAGES_TEMPLATES_ONLY,
    val scorecardEnabled: Boolean = AppPreferencesDefault.DisplayDefault.SCORECARD_ENABLED
)
