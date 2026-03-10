package com.done.weather.ui.compose_ui.camera_screen_vision

import android.graphics.Rect
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.done.weather.data.api.ApiHttpException
import com.done.weather.data.api.dto.CamifeyeSettingsDto
import com.done.weather.data.vision.PersonDetector
import com.done.weather.domain.repository.GroupLogRepository
import com.done.weather.domain.model.camera_detect.DetectionBox
import com.done.weather.domain.model.camera_detect.GroupRecord
import com.done.weather.domain.repository.CamifeyeRepository
import com.done.weather.domain.usecase.TrackGroupUseCase
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean

class CameraViewModel(
    private val detector: PersonDetector,
    private val tracker: TrackGroupUseCase,
    private val logRepository: GroupLogRepository,
    private val camifeyeRepository: CamifeyeRepository
) : ViewModel() {

    enum class Phase { Bootstrapping, Ready, Error }
    enum class SnackbarType { SUCCESS, ERROR, INFO }

    enum class LocationType { TEE, FAIRWAY, GREEN }

    data class HoleItem(
        val id: Int,
        val description: String,
        val tee: String,
        val fairway: String,
        val green: String
    )

    data class SettingsUiState(
        val holeId: Int? = null,
        val holeDescription: String = "",
        val locationType: LocationType = LocationType.FAIRWAY,
        val name: String = "Camera",
        val defaultGroupSize: Int = 4,
        val isSaving: Boolean = false,
        val errorText: String? = null
    )

    data class UiState(
        val phase: Phase = Phase.Bootstrapping,
        val currentTime: String = "--:--",

        val playersOnScreen: Int = 0,
        val currentGroupMax: Int = 0,
        val cooldownSeconds: Int = 0,

        val boxes: List<DetectionBox> = emptyList(),
        val log: List<GroupRecord> = emptyList(),

        val detectorMessage: String? = null,

        val bootstrapError: String? = null,

        val lastRoundError: String? = null,
        val canRetryLastRound: Boolean = false,

        // settings flow
        val isSettingsOpen: Boolean = false,
        val canCancelSettings: Boolean = true,
        val canOpenSettings: Boolean = false,

        val serverUrl: String = "ausapi.verifeye.info",
        val deviceId: String = "",

        val holes: List<HoleItem> = emptyList(),
        val settings: SettingsUiState = SettingsUiState()
    )

    sealed class UiEvent {
        data class ShowSnackbar(
            val type: SnackbarType,
            val message: String,
            val actionLabel: String? = null,
            val action: (() -> Unit)? = null
        ) : UiEvent()
    }

    private val _state = MutableStateFlow(
        UiState(
            phase = Phase.Bootstrapping,
            currentTime = nowTime(),
            log = logRepository.load()
        )
    )
    val state: StateFlow<UiState> = _state

    private val _events = MutableSharedFlow<UiEvent>(extraBufferCapacity = 32)
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()

    private val bootstrappedOnce = AtomicBoolean(false)

    // group timing
    private var groupFirstSeenMs: Long? = null
    private var groupLastSeenMs: Long? = null

    // pending round for retry
    private data class PendingRound(val firstSeen: Long, val lastSeen: Long, val groupCount: Int)
    private var pendingRound: PendingRound? = null

    /**
     * Вызывай из Activity один раз (после permissions).
     */
    fun bootstrap(
        deviceId: String,
        cameraName: String = "Camera",
        defaultGroupSize: Int = 4,
        serverUrl: String = "ausapi.verifeye.info"
    ) {
        if (!bootstrappedOnce.compareAndSet(false, true)) return

        _state.update {
            it.copy(
                deviceId = deviceId,
                serverUrl = serverUrl.trim().ifBlank { "ausapi.verifeye.info" },
                settings = it.settings.copy(
                    name = cameraName.ifBlank { "Camera" },
                    defaultGroupSize = defaultGroupSize.coerceIn(1, 99)
                )
            )
        }

        startBootstrap(withBackoff = false)
    }

    fun retryBootstrap() {
        startBootstrap(withBackoff = true)
    }

    fun openSettings() {
        _state.update { it.copy(isSettingsOpen = true) }
    }

    fun closeSettings() {
        val s = _state.value
        if (!s.canCancelSettings) return
        _state.update { it.copy(isSettingsOpen = false, settings = it.settings.copy(errorText = null)) }
    }

    fun updateServerUrl(url: String) {
        _state.update { it.copy(serverUrl = url.trim()) }
    }

    fun selectHole(holeId: Int) {
        val hole = _state.value.holes.firstOrNull { it.id == holeId } ?: return
        val currentType = _state.value.settings.locationType
        val newType = if (hole.hasLocation(currentType)) currentType else hole.firstAvailableLocationType()

        _state.update {
            it.copy(
                settings = it.settings.copy(
                    holeId = hole.id,
                    holeDescription = hole.description,
                    locationType = newType,
                    errorText = null
                )
            )
        }
    }

    fun selectLocationType(type: LocationType) {
        val hole = selectedHoleOrNull()
        if (hole != null && !hole.hasLocation(type)) return
        _state.update { it.copy(settings = it.settings.copy(locationType = type, errorText = null)) }
    }

    fun updateName(name: String) {
        _state.update { it.copy(settings = it.settings.copy(name = name, errorText = null)) }
    }

    fun updateDefaultGroupSize(size: Int) {
        _state.update { it.copy(settings = it.settings.copy(defaultGroupSize = size.coerceIn(1, 99), errorText = null)) }
    }

    fun saveSettings() {
        val s = _state.value
        val hole = selectedHoleOrNull()

        if (s.serverUrl.isBlank()) {
            _state.update { it.copy(settings = it.settings.copy(errorText = "Server URL is required")) }
            return
        }
        if (s.deviceId.isBlank()) {
            _state.update { it.copy(settings = it.settings.copy(errorText = "Device ID is required")) }
            return
        }
        if (hole == null) {
            _state.update { it.copy(settings = it.settings.copy(errorText = "Hole is required")) }
            return
        }

        val coordsRaw = when (s.settings.locationType) {
            LocationType.TEE -> hole.tee
            LocationType.FAIRWAY -> hole.fairway
            LocationType.GREEN -> hole.green
        }
        val coords = normalizeLocation(coordsRaw)
        if (coords.isBlank()) {
            _state.update { it.copy(settings = it.settings.copy(errorText = "Location is required")) }
            return
        }

        _state.update { it.copy(settings = it.settings.copy(isSaving = true, errorText = null)) }

        viewModelScope.launch {
            try {
                val payload = CamifeyeSettingsDto(
                    name = s.settings.name.ifBlank { "Camera" },
                    holeId = hole.id,
                    location = coords,
                    expectedGroupSize = s.settings.defaultGroupSize
                )

                withContext(Dispatchers.IO) {
                    val existingResult = camifeyeRepository.getSettings()
                    val existing = existingResult.getOrNull()

                    if (existing == null) {
                        camifeyeRepository.postSettings(payload).getOrElse { throw it }
                    } else {
                        camifeyeRepository.putSettings(payload).getOrElse { throw it }
                    }
                }

                // ✅ после первого SAVE камера разрешена
                _state.update {
                    it.copy(
                        isSettingsOpen = false,
                        canCancelSettings = true,
                        canOpenSettings = true,
                        phase = Phase.Ready,
                        settings = it.settings.copy(isSaving = false, errorText = null)
                    )
                }

                _events.tryEmit(UiEvent.ShowSnackbar(SnackbarType.SUCCESS, "Settings saved"))
            } catch (ce: CancellationException) {
                throw ce
            } catch (t: Throwable) {
                _state.update {
                    it.copy(
                        settings = it.settings.copy(
                            isSaving = false,
                            errorText = t.toReadableError()
                        )
                    )
                }
            }
        }
    }

    // ===== BOOTSTRAP INTERNAL =====
    private fun startBootstrap(withBackoff: Boolean) {
        _state.update {
            it.copy(
                phase = Phase.Bootstrapping,
                bootstrapError = null,
                lastRoundError = null,
                detectorMessage = null
            )
        }

        viewModelScope.launch {
            try {
                if (withBackoff) bootstrapWithBackoff() else bootstrapOnce()
            } catch (ce: CancellationException) {
                throw ce
            } catch (t: Throwable) {
                _state.update { it.copy(phase = Phase.Error, bootstrapError = t.toReadableError()) }
            }
        }
    }

    private suspend fun bootstrapWithBackoff() {
        val delays = listOf(500L, 1000L, 2000L, 4000L, 8000L)
        var last: Throwable? = null
        for (d in delays) {
            try {
                bootstrapOnce()
                return
            } catch (t: Throwable) {
                last = t
                delay(d)
            }
        }
        throw (last ?: IllegalStateException("Bootstrap failed"))
    }

    private suspend fun bootstrapOnce() = withContext(Dispatchers.IO) {
        // 1) holes
        val holesRaw = camifeyeRepository.getHoles().getOrElse { throw it }
        val holes = holesRaw.map {
            HoleItem(
                id = it.id,
                description = it.description.orEmpty(),
                tee = it.tee.orEmpty(),
                fairway = it.fairway.orEmpty(),
                green = it.green.orEmpty()
            )
        }

        // 2) settings
        val settingsResult = camifeyeRepository.getSettings()
        val settings = settingsResult.getOrNull()

        val firstHole = holes.firstOrNull()
        val notRegistered = settingsResult.exceptionOrNull()?.isNotRegisteredError() == true

        if (settings == null) {
            val firstType = firstHole?.firstAvailableLocationType() ?: LocationType.FAIRWAY

            // ✅ mandatory settings (если не зарегистрирован) — нельзя cancel
            _state.update { st ->
                st.copy(
                    holes = holes,
                    phase = Phase.Ready,
                    isSettingsOpen = true,
                    canCancelSettings = !notRegistered,
                    canOpenSettings = !notRegistered,
                    settings = st.settings.copy(
                        holeId = firstHole?.id,
                        holeDescription = firstHole?.description.orEmpty(),
                        locationType = firstType,
                        errorText = null,
                        isSaving = false
                    )
                )
            }
            return@withContext
        }

        // ✅ REQUIREMENT: even if settings exists -> open settings FIRST, block camera until Save
        val hole = holes.firstOrNull { it.id == settings.holeId } ?: firstHole
        val locType = detectLocationType(hole, settings.location)

        _state.update { st ->
            st.copy(
                holes = holes,
                phase = Phase.Ready,

                // ✅ show Settings first
                isSettingsOpen = true,

                // ✅ first time flow: force user to confirm Save
                canCancelSettings = false,

                // gear allowed (camera hidden anyway while dialog open)
                canOpenSettings = true,

                settings = st.settings.copy(
                    holeId = hole?.id,
                    holeDescription = hole?.description.orEmpty(),
                    locationType = locType,
                    name = settings.name.ifBlank { st.settings.name },
                    defaultGroupSize = settings.expectedGroupSize.coerceIn(1, 99),
                    errorText = null,
                    isSaving = false
                )
            )
        }
    }

    // ===== CAMERA FRAMES =====
    fun onFrame(imageProxy: ImageProxy) {
        viewModelScope.launch {
            try {
                val now = System.currentTimeMillis()

                val result = withContext(Dispatchers.Default) {
                    detector.detectPeople(imageProxy)
                }

                when (result) {
                    is PersonDetector.Result.Success -> {
                        val rects: List<Rect> = result.boxes

                        val imgW = imageProxy.width.toFloat().coerceAtLeast(1f)
                        val imgH = imageProxy.height.toFloat().coerceAtLeast(1f)

                        val newBoxes = rects.map {
                            DetectionBox(
                                left = (it.left / imgW).coerceIn(0f, 1f),
                                top = (it.top / imgH).coerceIn(0f, 1f),
                                right = (it.right / imgW).coerceIn(0f, 1f),
                                bottom = (it.bottom / imgH).coerceIn(0f, 1f)
                            )
                        }

                        if (rects.isNotEmpty() && !tracker.isCooldown(now)) {
                            if (groupFirstSeenMs == null) groupFirstSeenMs = now
                            groupLastSeenMs = now
                        }

                        val event = tracker.onPeopleCount(rects.size, now)
                        val cooldown = if (tracker.isCooldown(now)) tracker.cooldownRemainingSeconds(now) else 0

                        _state.update {
                            it.copy(
                                currentTime = nowTime(),
                                playersOnScreen = rects.size,
                                currentGroupMax = tracker.currentMax(),
                                cooldownSeconds = cooldown,
                                boxes = newBoxes,
                                detectorMessage = null
                            )
                        }

                        if (event is TrackGroupUseCase.Event.GroupClosed) {
                            handleGroupClosed(event.maxPlayers)
                        }
                    }

                    is PersonDetector.Result.Error -> {
                        _state.update { it.copy(detectorMessage = result.message) }
                    }
                }
            } catch (ce: CancellationException) {
                throw ce
            } catch (t: Throwable) {
                _state.update { it.copy(detectorMessage = "Frame crash: ${t.message}") }
            } finally {
                imageProxy.close()
            }
        }
    }

    // ===== ROUND SEND =====
    private fun handleGroupClosed(maxPlayers: Int) {
        val firstSeen = groupFirstSeenMs
        val lastSeen = groupLastSeenMs
        groupFirstSeenMs = null
        groupLastSeenMs = null

        logRepository.append(GroupRecord(time = nowTime(), maxPlayers = maxPlayers))
        _state.update { it.copy(log = logRepository.load()) }

        if (firstSeen == null || lastSeen == null) return
        sendRound(firstSeen, lastSeen, maxPlayers)
    }

    private fun sendRound(firstSeen: Long, lastSeen: Long, groupCount: Int) {
        pendingRound = null
        _state.update { it.copy(canRetryLastRound = false, lastRoundError = null) }

        viewModelScope.launch {
            try {
                val res = withContext(Dispatchers.IO) {
                    camifeyeRepository.postRound(firstSeen, lastSeen, groupCount)
                }
                if (res.isFailure) throw (res.exceptionOrNull() ?: IllegalStateException("Round error"))

                _events.tryEmit(UiEvent.ShowSnackbar(SnackbarType.SUCCESS, "Round sent: $groupCount"))
            } catch (ce: CancellationException) {
                throw ce
            } catch (t: Throwable) {
                val err = t.toReadableError()

                pendingRound = PendingRound(firstSeen, lastSeen, groupCount)
                _state.update { it.copy(canRetryLastRound = true, lastRoundError = err) }

                _events.tryEmit(
                    UiEvent.ShowSnackbar(
                        type = SnackbarType.ERROR,
                        message = "Failed to send round: $err",
                        actionLabel = "Retry",
                        action = { retryLastRound() }
                    )
                )
            }
        }
    }

    fun retryLastRound() {
        val p = pendingRound ?: return
        pendingRound = null
        _state.update { it.copy(canRetryLastRound = false) }
        sendRound(p.firstSeen, p.lastSeen, p.groupCount)
    }

    // ===== HELPERS =====
    private fun nowTime(): String =
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

    private fun normalizeLocation(raw: String): String {
        val parts = raw.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        return if (parts.size == 2) "${parts[0]},${parts[1]}" else raw.trim()
    }

    private fun detectLocationType(hole: HoleItem?, location: String?): LocationType {
        if (hole == null || location.isNullOrBlank()) return LocationType.FAIRWAY
        val locNorm = normalizeLocation(location)
        return when (locNorm) {
            normalizeLocation(hole.tee) -> LocationType.TEE
            normalizeLocation(hole.green) -> LocationType.GREEN
            normalizeLocation(hole.fairway) -> LocationType.FAIRWAY
            else -> hole.firstAvailableLocationType()
        }
    }

    private fun selectedHoleOrNull(): HoleItem? {
        val id = _state.value.settings.holeId ?: return null
        return _state.value.holes.firstOrNull { it.id == id }
    }

    private fun HoleItem.hasLocation(type: LocationType): Boolean = when (type) {
        LocationType.TEE -> tee.trim().isNotEmpty()
        LocationType.FAIRWAY -> fairway.trim().isNotEmpty()
        LocationType.GREEN -> green.trim().isNotEmpty()
    }

    private fun HoleItem.firstAvailableLocationType(): LocationType = when {
        fairway.trim().isNotEmpty() -> LocationType.FAIRWAY
        green.trim().isNotEmpty() -> LocationType.GREEN
        tee.trim().isNotEmpty() -> LocationType.TEE
        else -> LocationType.FAIRWAY
    }

    private fun Throwable.toReadableError(): String = when (this) {
        is ApiHttpException -> "HTTP $code: ${responseBody.ifBlank { "(empty body)" }}"
        is ResponseException -> "HTTP ${response.status.value}: ${message ?: "ResponseException"}"
        else -> message ?: this::class.java.simpleName
    }

    private fun Throwable.isNotRegisteredError(): Boolean {
        return (this is ResponseException && this.response.status.value == 404)
    }
}
