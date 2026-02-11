package com.done.weather.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.done.weather.domain.WifiInfo
import com.done.weather.ui.compose_ui.camera_screen_vision.CameraScreen
import com.done.weather.ui.compose_ui.camera_screen_vision.CameraViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class CameraDetectionActivity : ComponentActivity() {

    private val vm: CameraViewModel by viewModel()
    private var hasCameraPermission by mutableStateOf(false)

    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            hasCameraPermission = granted
            if (granted) {
                startBootstrap()
            }
            // если denied — bootstrap НЕ запускаем
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        hasCameraPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        setContent {
            CameraScreen(
                hasCameraPermission = hasCameraPermission,
                viewModel = vm
            )
        }

        if (!hasCameraPermission) {
            requestCameraPermission.launch(Manifest.permission.CAMERA)
        } else {
            startBootstrap()
        }
    }

    private fun startBootstrap() {
        val deviceId = WifiInfo.getDeviceId(this)

        vm.bootstrap(
            deviceId = deviceId,
            cameraName = deviceId,
            defaultGroupSize = 4,
            serverUrl = "ausapi.verifeye.info"
        )
    }
}
