package com.done.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import com.done.domain.models.*
import com.done.domain.usecase.AddPlayerLocalUseCase
import com.done.domain.usecase.CreateRoundAndSyncUseCase
import com.done.domain.usecase.RemovePlayerLocalUseCase
import java.util.UUID

data class CreateRoundUi(
    val course: String = "Cronulla Golf club",
    val date: LocalDate = LocalDate.now(),
    val tees: TeeColour? = null,
    val holes: Int? = null,
    val roundType: RoundType? = null,
    val players: List<Player> = emptyList(),
    val roundId: String? = null,               // ← добавили: понимаем создан ли раунд
    val isValid: Boolean = false
)

@HiltViewModel
class CreateRoundViewModel @Inject constructor(
    private val createRoundAndSync: CreateRoundAndSyncUseCase,
    private val addPlayerLocalUseCase: AddPlayerLocalUseCase,
    private val removePlayerLocalUseCase: RemovePlayerLocalUseCase
) : ViewModel() {

    private val _ui = MutableStateFlow(CreateRoundUi())
    val ui: StateFlow<CreateRoundUi> = _ui.asStateFlow()

    init {
        _ui.update { cur ->
            val seeded = cur.copy(
                tees = cur.tees ?: TeeColour.RED,
                holes = cur.holes ?: 18,
                roundType = cur.roundType ?: RoundType.STABLEFORD
            )
            seeded.copy(isValid = validate(seeded))
        }
    }

    /* -------- настройки формы -------- */
    fun setTees(v: TeeColour) = update { it.copy(tees = v) }
    fun setHoles(v: Int)      = update { it.copy(holes = v) }
    fun setType(v: RoundType) = update { it.copy(roundType = v) }


    fun addPlayer(p: Player) {
        val rid = _ui.value.roundId
        if (rid == null) {
            // раунд ещё не создан → только UI
            update { it.copy(players = it.players + p) }
        } else {
            // раунд уже создан → пишем в Room и обновляем UI
            viewModelScope.launch {
                addPlayerLocalUseCase(rid, p)
                update { it.copy(players = it.players + p) }
            }
        }
    }

    fun removePlayer(playerId: String) {
        val rid = _ui.value.roundId
        if (rid == null) {
            // только UI
            update { it.copy(players = it.players.filterNot { p -> p.id == playerId }) }
        } else {
            // Room + UI
            viewModelScope.launch {
                removePlayerLocalUseCase(rid, playerId)
                update { it.copy(players = it.players.filterNot { p -> p.id == playerId }) }
            }
        }
    }

    /* -------- создание раунда + синк -------- */
    fun createRound(onCreated: (String) -> Unit) {
        val s = _ui.value
        if (!s.isValid) return
        viewModelScope.launch {
            val meta = RoundMeta(
                id = UUID.randomUUID().toString(),
                course = s.course,
                date = s.date,
                startTime = LocalDateTime.now(),
                holes = s.holes!!,
                tee = s.tees!!,
                type = s.roundType!!
            )
            val card = createRoundAndSync(meta, s.players)
            // сохраняем roundId в стейт, чтобы дальше add/remove ходили в Room
            update { it.copy(roundId = card.round.id) }
            onCreated(card.round.id)
        }
    }

    /* -------- вспомогательные -------- */
    private fun update(f: (CreateRoundUi) -> CreateRoundUi) {
        val next = f(_ui.value).let { it.copy(isValid = validate(it)) }
        _ui.value = next
    }

    private fun validate(u: CreateRoundUi): Boolean =
        u.course.isNotBlank() &&
                u.tees != null &&
                u.holes != null &&
                u.roundType != null &&
                u.players.isNotEmpty()
}
