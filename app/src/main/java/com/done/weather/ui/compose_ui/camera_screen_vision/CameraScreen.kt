package com.done.weather.ui.compose_ui.camera_screen_vision

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalGetImage::class, ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    hasCameraPermission: Boolean,
    viewModel: CameraViewModel
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    // keep screen on
    val view = LocalView.current
    DisposableEffect(Unit) {
        val old = view.keepScreenOn
        view.keepScreenOn = true
        onDispose { view.keepScreenOn = old }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    var lastSnackbarType by remember { mutableStateOf(CameraViewModel.SnackbarType.INFO) }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { ev ->
            when (ev) {
                is CameraViewModel.UiEvent.ShowSnackbar -> {
                    lastSnackbarType = ev.type
                    val res = snackbarHostState.showSnackbar(
                        message = ev.message,
                        actionLabel = ev.actionLabel,
                        withDismissAction = true,
                        duration = SnackbarDuration.Short
                    )
                    if (res == SnackbarResult.ActionPerformed) {
                        ev.action?.invoke()
                    }
                }
            }
        }
    }

    if (!hasCameraPermission) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Camera permission required", fontSize = 18.sp, color = Color.White)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Разрешение выдаётся на уровне Activity 4949086t4908.",
                    fontSize = 14.sp,
                    color = Color.LightGray
                )
            }
        }
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // ✅ IMPORTANT: camera is visible ONLY when Ready AND settings dialog is NOT open
        if (uiState.phase == CameraViewModel.Phase.Ready && !uiState.isSettingsOpen) {
            CameraPreview { imageProxy: ImageProxy ->
                if (imageProxy.image == null) {
                    imageProxy.close()
                    return@CameraPreview
                }
                viewModel.onFrame(imageProxy)
            }

            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx -> OverlayView(ctx) },
                update = { it.setBoxes(uiState.boxes) }
            )

            if (uiState.canOpenSettings) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(34.dp)
                        .background(Color(0x66000000), RoundedCornerShape(8.dp))
                        .clickable { viewModel.openSettings() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.White
                    )
                }
            }

            RightStatusPanel(
                uiState = uiState,
                onRetryRound = { viewModel.retryLastRound() }
            )
        }

        if (uiState.phase == CameraViewModel.Phase.Bootstrapping) {
            LoadingOverlay(text = "Loading…")
        }

        if (uiState.phase == CameraViewModel.Phase.Error) {
            BootstrapErrorOverlay(
                error = uiState.bootstrapError ?: "Unknown error",
                onRetry = { viewModel.retryBootstrap() }
            )
        }

        // Settings dialog is always on top and blocks camera
        if (uiState.isSettingsOpen) {
            SettingsDialog(
                state = uiState.settings,
                holes = uiState.holes,
                deviceId = uiState.deviceId,
                serverUrl = uiState.serverUrl,
                onDismiss = { viewModel.closeSettings() },
                onServerUrlChanged = { viewModel.updateServerUrl(it) },
                onHoleSelected = { viewModel.selectHole(it) },
                onLocationTypeSelected = { viewModel.selectLocationType(it) },
                onNameChanged = { viewModel.updateName(it) },
                onGroupSizeChange = { viewModel.updateDefaultGroupSize(it) },
                onSave = { viewModel.saveSettings() },
                allowCancel = uiState.canCancelSettings
            )
        }

        SnackbarHost(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            hostState = snackbarHostState,
            snackbar = { data ->
                val containerColor = when (lastSnackbarType) {
                    CameraViewModel.SnackbarType.SUCCESS -> Color(0xFF1B5E20)
                    CameraViewModel.SnackbarType.ERROR -> Color(0xFFB71C1C)
                    CameraViewModel.SnackbarType.INFO -> Color(0xFF2A2A2A)
                }
                Snackbar(
                    snackbarData = data,
                    containerColor = containerColor,
                    contentColor = Color.White,
                    actionColor = Color.White
                )
            }
        )
    }
}

@Composable
private fun RightStatusPanel(
    uiState: CameraViewModel.UiState,
    onRetryRound: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 54.dp, end = 12.dp)
                .width(260.dp)
                .background(Color(0x88000000), RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Text(
                "Time: ${uiState.currentTime}",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(6.dp))
            Text("Current group max: ${uiState.currentGroupMax}", color = Color.White, fontSize = 14.sp)
            Text("Players on screen: ${uiState.playersOnScreen}", color = Color.White, fontSize = 14.sp)

            if (uiState.cooldownSeconds > 0) {
                Spacer(Modifier.height(4.dp))
                Text("Cool-down: ${uiState.cooldownSeconds}s", color = Color.White, fontSize = 14.sp)
            }

            uiState.detectorMessage?.let {
                Spacer(Modifier.height(8.dp))
                Text("Detector: $it", color = Color(0xFFFFD54F), fontSize = 13.sp)
            }

            uiState.lastRoundError?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = Color(0xFFFF8A80), fontSize = 13.sp)
            }

            if (uiState.canRetryLastRound) {
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x33FFFFFF), RoundedCornerShape(8.dp))
                        .clickable { onRetryRound() }
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Failed to send round", color = Color.White, fontSize = 13.sp)
                    Text("Retry", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(12.dp))
            Text("Groups log", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 70.dp, max = 240.dp)
            ) {
                items(uiState.log) { item ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(item.time, color = Color.White, fontSize = 13.sp)
                        Text(item.maxPlayers.toString(), color = Color.White, fontSize = 13.sp)
                    }
                    Spacer(Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun LoadingOverlay(text: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x66000000)),
        contentAlignment = Alignment.Center
    ) {
        Surface(color = Color(0xCC000000), shape = RoundedCornerShape(10.dp)) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(Modifier.width(12.dp))
                Text(text, color = Color.White, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun BootstrapErrorOverlay(
    error: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCC000000)),
        contentAlignment = Alignment.Center
    ) {
        Surface(color = Color(0xFF111111), shape = RoundedCornerShape(12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Bootstrap failed", color = Color.White, fontSize = 18.sp)
                Spacer(Modifier.height(8.dp))
                Text(error, color = Color(0xFFFFD54F), fontSize = 14.sp)
                Spacer(Modifier.height(12.dp))
                Text(
                    "Retry",
                    color = Color.White,
                    modifier = Modifier
                        .background(Color(0xFF2A2A2A), RoundedCornerShape(8.dp))
                        .clickable { onRetry() }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                )
            }
        }
    }
}

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsDialog(
    state: CameraViewModel.SettingsUiState,
    holes: List<CameraViewModel.HoleItem>,
    deviceId: String,
    serverUrl: String,
    onDismiss: () -> Unit,
    onServerUrlChanged: (String) -> Unit,
    onHoleSelected: (Int) -> Unit,
    onLocationTypeSelected: (CameraViewModel.LocationType) -> Unit,
    onNameChanged: (String) -> Unit,
    onGroupSizeChange: (Int) -> Unit,
    onSave: () -> Unit,
    allowCancel: Boolean
) {
    var holesExpanded by remember { mutableStateOf(false) }

    val selectedHole = remember(holes, state.holeId) {
        holes.firstOrNull { it.id == state.holeId }
    }

    val locationOptions = remember(selectedHole) {
        buildLocationOptions(selectedHole)
    }

    LaunchedEffect(locationOptions) {
        if (locationOptions.isNotEmpty() && locationOptions.none { it.type == state.locationType }) {
            onLocationTypeSelected(locationOptions.first().type)
        }
    }

    AlertDialog(
        onDismissRequest = { if (allowCancel) onDismiss() },
        title = { Text("Settings") },
        text = {
            Column {
                OutlinedTextField(
                    value = serverUrl,
                    onValueChange = onServerUrlChanged,
                    label = { Text("Server URL") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = deviceId,
                    onValueChange = {},
                    enabled = false,
                    label = { Text("Device ID") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                // ✅ HOLE PICKER (no OutlinedTextField)
                Text("Hole", fontSize = 13.sp, color = Color(0xFF6B6B6B))
                Spacer(Modifier.height(6.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x11000000), RoundedCornerShape(12.dp))
                            .clickable { holesExpanded = true }
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = state.holeDescription.ifBlank { "Select hole" },
                            fontSize = 15.sp
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Open holes"
                        )
                    }

                    DropdownMenu(
                        expanded = holesExpanded,
                        onDismissRequest = { holesExpanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        holes.forEach { h ->
                            DropdownMenuItem(
                                text = { Text(h.description) },
                                onClick = {
                                    holesExpanded = false
                                    onHoleSelected(h.id)
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                Text("Location")
                Spacer(Modifier.height(6.dp))

                if (locationOptions.isEmpty()) {
                    Text("No location data for selected hole", color = Color(0xFFFF8A80), fontSize = 13.sp)
                } else {
                    SingleChoiceSegmentedButtonRow {
                        locationOptions.forEachIndexed { index, opt ->
                            SegmentedButton(
                                selected = state.locationType == opt.type,
                                onClick = { onLocationTypeSelected(opt.type) },
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = locationOptions.size)
                            ) { Text(opt.title) }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = state.name,
                    onValueChange = onNameChanged,
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                Text("Default group size")
                Spacer(Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "−",
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0x22000000), RoundedCornerShape(10.dp))
                            .clickable { onGroupSizeChange((state.defaultGroupSize - 1).coerceAtLeast(1)) }
                            .wrapContentSize(Alignment.Center),
                        fontSize = 18.sp
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(state.defaultGroupSize.toString(), fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "+",
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0x22000000), RoundedCornerShape(10.dp))
                            .clickable { onGroupSizeChange((state.defaultGroupSize + 1).coerceAtMost(99)) }
                            .wrapContentSize(Alignment.Center),
                        fontSize = 18.sp
                    )
                }

                state.errorText?.let {
                    Spacer(Modifier.height(10.dp))
                    Text(it, color = Color(0xFFB71C1C), fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onSave, enabled = !state.isSaving) {
                Text(if (state.isSaving) "Saving…" else "Save")
            }
        },
        dismissButton = {
            if (allowCancel) {
                TextButton(onClick = onDismiss, enabled = !state.isSaving) { Text("Cancel") }
            }
        }
    )
}

private data class LocationOption(
    val type: CameraViewModel.LocationType,
    val title: String,
    val coords: String
)

private fun buildLocationOptions(hole: CameraViewModel.HoleItem?): List<LocationOption> {
    if (hole == null) return emptyList()

    val res = mutableListOf<LocationOption>()
    hole.tee.trim().takeIf { it.isNotEmpty() }?.let {
        res += LocationOption(CameraViewModel.LocationType.TEE, "Tee", it)
    }
    hole.fairway.trim().takeIf { it.isNotEmpty() }?.let {
        res += LocationOption(CameraViewModel.LocationType.FAIRWAY, "Fairway", it)
    }
    hole.green.trim().takeIf { it.isNotEmpty() }?.let {
        res += LocationOption(CameraViewModel.LocationType.GREEN, "Green", it)
    }
    return res
}
