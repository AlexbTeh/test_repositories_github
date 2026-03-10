package com.done.weather.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.done.weather.domain.WifiInfo
import com.done.weather.domain.repository.BaseSettingsRepository
import com.done.weather.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import timber.log.Timber

// Если у тебя эти классы в другом пакете — поправь импорты:
import com.done.weather.AppConstant
import com.done.weather.utils.ResourceUtil
import com.done.weather.utils.SUtil.installApp
import com.done.weather.utils.SUtil.startApp
import com.done.weather.utils.SUtil.suAvailable

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SplashActivity"
        private const val AUTH_TAG = "Auth"
    }

    private val authRepository: AuthRepository by inject()
    private val settings: BaseSettingsRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenCreated {
            // 1) bootstrap bearer token (как у тебя)
            bootstrapBearerTokenIfNeeded()

            // 2) launcher install/update (без downgrade)
            runCatching {
                installLauncherIfNeededWithoutDowngrade()
            }.onFailure { e ->
                Timber.tag(TAG).e(e, "Launcher install flow failed (ignored)")
                // важно: не роняем приложение из-за launcher
            }

            // 3) переход дальше
            goNext()
        }
    }

    private suspend fun bootstrapBearerTokenIfNeeded() {
        val existing = settings.bearerToken.first().trim()

        if (existing.isNotBlank()) {
            Timber.tag(AUTH_TAG).d("Bearer already exists: ${existing.take(10)}...")
            return
        }

        val deviceId = WifiInfo.getDeviceId(this)
        Timber.tag(AUTH_TAG).d("Bearer empty -> authenticate deviceId=$deviceId")

        val token = authRepository.authenticate(deviceId).getOrThrow()
        settings.updateBearerToken(token)

        Timber.tag(AUTH_TAG).d("Bearer saved: ${token.take(10)}...")
    }

    /**
     * ✅ Безопасная установка launcher.apk из assets:
     * - сравниваем versionCode установленного launcher и apk-файла
     * - ставим ТОЛЬКО если apk новее
     * - если apk старее/равен -> SKIP (никаких INSTALL_FAILED_VERSION_DOWNGRADE)
     */
    private suspend fun installLauncherIfNeededWithoutDowngrade(): Boolean = withContext(Dispatchers.IO) {
        if (!suAvailable()) {
            Timber.tag(TAG).d("suAvailable=false -> skip launcher install")
            return@withContext false
        }

        val pm = packageManager

        // путь куда ResourceUtil кладёт файлы (как в VGPS)
        val launcherApkPath = ResourceUtil.getResFile("launcher.apk")
        if (launcherApkPath.isNullOrBlank()) {
            Timber.tag(TAG).w("launcher.apk path is null -> skip")
            return@withContext false
        }

        // скопировать из assets в файл (как в VGPS)
        runCatching {
            ResourceUtil.copyAssetFile(
                this@SplashActivity,
                AppConstant.LAUNCHER_APP_NAME_IN_ASSET,
                launcherApkPath
            )
        }.onFailure { e ->
            Timber.tag(TAG).e(e, "copyAssetFile failed -> skip launcher install")
            return@withContext false
        }

        val installedVc = getInstalledVersionCode(pm, AppConstant.LAUNCHER_PACKAGE_NAME) ?: -1L
        val apkVc = getApkVersionCode(pm, launcherApkPath) ?: -1L

        Timber.tag(TAG).d("Launcher versions: installed=$installedVc apk=$apkVc")

        if (apkVc <= 0) {
            Timber.tag(TAG).w("apkVc invalid ($apkVc) -> skip launcher install")
            return@withContext false
        }

        // ✅ ключ: не даём downgrade
        if (apkVc <= installedVc) {
            Timber.tag(TAG).i("Skip launcher install: device has same/newer version")
            return@withContext true
        }

        Timber.tag(TAG).i("Installing launcher update: apkVc=$apkVc > installedVc=$installedVc")
        runCatching {
            installApp(launcherApkPath, true)
        }.onFailure { e ->
            Timber.tag(TAG).e(e, "installApp failed")
            // даже если install упал — app дальше живёт
            return@withContext false
        }

        // если у тебя по логике нужно запускать launcher после установки:
        runCatching {
            startApp(AppConstant.LAUNCHER_PACKAGE_NAME)
        }.onFailure { e ->
            Timber.tag(TAG).w(e, "startApp launcher failed (ignored)")
        }

        true
    }

    private fun getInstalledVersionCode(pm: PackageManager, packageName: String): Long? {
        return try {
            val pi = pm.getPackageInfo(packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) pi.longVersionCode
            else @Suppress("DEPRECATION") pi.versionCode.toLong()
        } catch (_: Throwable) {
            null
        }
    }

    private fun getApkVersionCode(pm: PackageManager, apkPath: String): Long? {
        return try {
            val pi = pm.getPackageArchiveInfo(apkPath, 0) ?: return null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) pi.longVersionCode
            else @Suppress("DEPRECATION") pi.versionCode.toLong()
        } catch (_: Throwable) {
            null
        }
    }

    private fun goNext() {
        startActivity(
            Intent(this, CameraDetectionActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
        finish()
    }
}
