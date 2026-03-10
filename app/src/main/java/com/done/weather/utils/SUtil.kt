package com.done.weather.utils

import android.content.Context
import com.done.weather.AppConstant
import java.util.Locale

object SUtil {

    private const val LOG_TAG = "SUtil"

    @JvmStatic
    fun suAvailable(): Boolean {
        return SuCmd.available()
    }

    @JvmStatic
    fun enableGPS() {
        SuCmd.run("settings put secure location_providers_allowed gps")
    }

    fun setCpuMode(cpuMode: CpuMode) {
        val availableCPUs = Runtime.getRuntime().availableProcessors()
        // В твоём коде было (0..availableCPUs) — это включает лишний индекс.
        // Правильно: 0 until availableCPUs
        val cmds = (0 until availableCPUs).map { cpuIndex ->
            "echo ${cpuMode.name.lowercase()} > /sys/devices/system/cpu/cpu$cpuIndex/cpufreq/scaling_governor"
        }
        SuCmd.runAll(cmds)
    }

    @JvmStatic
    fun disableGPS() {
        SuCmd.run("settings put secure location_providers_allowed ' '")
    }

    @JvmStatic
    fun enableSystemUI() {
        SuCmd.run("wm overscan reset")
    }

    @JvmStatic
    fun disableSystemUI(context: Context) {
        val navHeight =
            context.resources.getDimensionPixelSizeOrNull(
                "navigation_bar_height",
                "dimen",
                "android"
            ) ?: 0

        val cmd = String.format(
            Locale.getDefault(),
            "wm overscan -%d,0,-%d,0",
            navHeight,
            navHeight
        )
        SuCmd.run(cmd)
    }

    @JvmStatic
    fun rebootSystem() {
        SuCmd.run("reboot")
    }

    @JvmStatic
    fun shutdownSystem() {
        SuCmd.run("svc power shutdown")
    }

    fun changeDPI(dpi: Int) {
        SuCmd.run(String.format(Locale.getDefault(), "wm density %d && reboot", dpi))
    }

    fun installApp(filePath: String, removeAfterInstall: Boolean) {
        val cmd = String.format(
            Locale.getDefault(),
            "pm install -r %s%s",
            filePath,
            if (removeAfterInstall) " && rm $filePath" else ""
        )
        SuCmd.run(cmd)
    }

    fun startApp(packageName: String?) {
        if (packageName.isNullOrBlank()) return
        val cmd = String.format(
            Locale.getDefault(),
            "am start -n %s/%s.MainActivity",
            packageName,
            packageName
        )
        SuCmd.run(cmd)
    }

    fun sendReinstallBroadcast(version: Long) {
        val launcherPackageName = "info.verifeye.vgps.launcher"
        val command = String.format(
            Locale.getDefault(),
            "am broadcast -a I_want_to_launch_app -n '%s/%s.MainAppInstallReceiver' --es 'package' '%s' --ei 'version' %d",
            launcherPackageName,
            launcherPackageName,
            AppConstant.APP_PACKAGE_NAME,
            version
        )
        SuCmd.run(command)
    }
}

enum class CpuMode {
    POWERSAVE, ONDEMAND, USERSPACE, INTERACTIVE, PERFORMANCE
}
