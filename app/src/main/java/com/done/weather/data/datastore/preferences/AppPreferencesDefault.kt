package com.done.weather.data.datastore.preferences

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object AppPreferencesDefault {

    object DisplayDefault {
        const val SHOW_HOLE_FRONT_DISTANCE: Boolean = true
        const val SHOW_HOLE_BACK_DISTANCE: Boolean = true
        const val ALLOW_SWITCH_UNITS: Boolean = true
        const val USE_MILITARY_TIME_FORMAT: Boolean = false
        const val SHOW_HOLE_TIMER: Boolean = true
        const val SHOW_CONNECTION_ICONS: Boolean = true
        const val USE_MESSAGES_TEMPLATES_ONLY: Boolean = false
        const val SCORECARD_ENABLED: Boolean = false
    }

    object DemoDefault {
        const val ENABLED: Boolean = false
        const val STAND_AT_FIRST_DEMO_POINT: Boolean = false
        const val SHOW_PATH: Boolean = false
        const val SPEED: Float = 8F
        const val TIME_SCALE: Float = 1F
        const val SEND_LOCATION: Boolean = false
    }

    object GpsDefault {
        const val MIN_ACCURACY: Float = 10F
        const val MIN_DISTANCE_CHANGE: Float = 1F
        const val MAX_SPEED: Float = 30F
        val HOMEBASE_RESET_ROUND_TIME: Duration = 10.minutes
        val LOCATION_UPDATE_PERIOD: Duration = 2.seconds
    }

    object LogDefault {
        val DEVICE_SYNC_PERIOD: Duration = 120.minutes
        val LOCATION_UPLOAD_PERIOD_SIM: Duration = 60.seconds
        val LOCATION_UPLOAD_PERIOD_WIFI: Duration = 60.seconds
        val LOCATION_REPORTING_PERIOD: Duration = 30.seconds
        const val LOCATION_REPORTING_BATCH_SIZE: Int = 100
        const val MIN_SPEED: Float = 6F
        const val MAX_SPEED: Float = 18F
    }

    object CourseUpdateDefault {
        val COURSE_UPDATE_PERIOD: Duration = 30.minutes
        val BACKGROUND_UPDATE_PERIOD: Duration = 5.minutes
    }

    object OtherCartsDefault {
        const val ENABLED: Boolean = false
        val UPDATE_PERIOD: Duration = 60.seconds
    }

    object NetworkDefault {
        const val LOW_MOBILE_DATA_CONSUMPTION = false
    }
}