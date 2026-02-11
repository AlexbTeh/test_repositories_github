package com.done.weather

import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object AppConstant {
    //Min days for notifying user about licence expiration
    const val LICENSE_DAYS_MIN_VALUE = 10

    // delays
    const val DELAY_EXIT = 2000
    val GOLF_BOUNDING_ANIMATION = 3.seconds
    const val GPS_RECT_BOUNDING_ANIMATION = 3000
    const val DELAY_ROOT_REQUEST = 2000
    val DELAY_WIFI_STATUS = 5.seconds
    val DELAY_BATTERY_INFO = 10.seconds
    val DELAY_DISPLAYED_BATTERY_STATUS = 5.seconds
    val SELECT_HOLE_TIME = 2.seconds
    val STATE_FLOW_SUBSCRIBER_TIMEOUT = 5.seconds
    val DEMO_START_DELAY = 5.seconds
    val SHOW_MAIN_DELAY = 2.seconds
    val HOLE_SWITCH_COOLDOWN_SECONDS = 5.seconds

    val SATELLITE_INFO_UPDATE_PERIOD = 5.seconds
    val REQUEST_FLAGS_PERIOD = 30.seconds
    val DEVICE_SYNC_ERROR_PERIOD = 10.minutes
    val UPLOAD_CAM_MEDIA_PERIOD = 30.seconds
    val UPLOAD_CART_POSITION_ERROR_PERIOD = 30.seconds
    val CHECK_MESSAGES_TEMPLATES_UPDATE_PERIOD = 30.minutes
    val LOCATION_RECEPTION_TIMEOUT = 15.seconds

    val LOG_RETENTION_THRESHOLD_DAYS = 10.days

    // Radius
    const val GREEN_RADIUS: Float = 10F

    // extra keys
    const val EK_ID = "EK_ID"
    const val EK_DATA = "EK_DATA"
    const val EK_DATA1 = "EK_DATA1"
    const val EK_DATA2 = "EK_DATA2"
    const val EK_EXIT = "EK_EXIT"

    // request code
    const val REQUEST_SELECT_HOLE = 100
    const val REQUEST_CHECK_GPS_SETTINGS = 102
    const val REQUEST_SYSTEM_SETTING = 103

    // broadcast
    const val MYACTION_FONT_SIZE_CHANGED = "info.verifeye.vgps.MYACTION_FONT_SIZE_CHANGED"
    const val MYACTION_CLOSE_BATTERY_PAGE = "info.verifeye.vgps.MYACTION_CLOSE_BATTERY_PAGE"
    const val MYACTION_CLOSE_POP_NOTIFICATION = "info.verifeye.vgps.MYACTION_CLOSE_POP_NOTIFICATION"
    const val MYACTION_ADD_BLE_DEVICE_COMMAND = "info.verifeye.vgps.MYACTION_ADD_BLE_DEVICE_COMMAND"
    const val MYACTION_INIT_SHUTDOWN_RECEIVER = "info.verifeye.vgps.MYACTION_INIT_SHUTDOWN_RECEIVER"

    // file path format
    const val COURSE_FILE_PATH_FORMAT = "%s/%s.xml"
    const val WELCOME_IMAGE_PATH_FORMAT = "%s/images/welcome.jpg"
    const val OVERVIEW_IMAGE_PATH_FORMAT = "%s/images/course.jpg"
    const val PHONES_BACKGROUND_PATH_FORMAT = "%s/images/phones_background.jpg"
    const val HOLE_IMAGE_PATH_FORMAT = "%s/images/%d/h%d.jpg"
    const val GREEN_IMAGE_PATH_FORMAT = "%s/images/%d/g%d.jpg"
    const val FLYOVER_FILE_PATH_FORMAT = "%s/images/%d/f%d.mp4"
    const val ADS_FILE_PATH_FORMAT = "%s/ads/%s"

    const val UNDEFINED = -1

    // for vGPS
    const val APP_PACKAGE_NAME = "info.verifeye.vgps"

    // for launcher
    const val LAUNCHER_PACKAGE_NAME = "info.verifeye.vgps.launcher"
    const val LAUNCHER_APP_NAME_IN_ASSET = "launcher-V-15.apk"

    // media extensions
    const val EXT_3GP = ".3gp"
    const val EXT_MP4 = ".mp4"
    const val EXT_JPG = ".jpg"
    const val EXT_PNG = ".png"

    // resource file
    const val COMPANY_DIR = "On-Pin/"
    const val ROOT_DIR = "vGPS/"
    const val COMMON_LOG = "common_log/"
    const val LOGS_DIR = "logs/"
    const val LOGS_ARCHIVE = "logs.zip"
    const val API_LOG_FILENAME = "api.log"
    const val GPS_LOG_FILENAME = "gps.log"
    const val LOGCAT_LOG_FILENAME = "logcat.log"
    const val TOAST_LOG_FILENAME = "toast.log"
    const val BLE_LOG_FILENAME = "ble.log"
    const val CRASH_REPORT_FILENAME = "crash_report.txt"
    const val GPSTEST = "gpstest/"
    const val BLUETOOTH = "bluetooth/"
    const val BLUETOOTH_DEVICE = "bluetooth-device/"
    const val CAMERA = "camera/"
    const val BASE_SETTINGS_NAME = "app_base_settings.json"
    const val SETTINGS_FILENAME = "app_settings.json"
    const val PREFERENCES_FILENAME = "app_preferences.json"
}
