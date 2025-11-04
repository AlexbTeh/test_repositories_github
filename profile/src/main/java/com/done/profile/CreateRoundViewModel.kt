package com.done.profile


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import com.done.domain.models.*
import com.done.domain.repository.RoundsRepository
import java.util.UUID

data class CreateRoundUi(
    val course: String = "Cronulla Golf club",
    val date: LocalDate = LocalDate.now(),
    val tees: TeeColour? = null,
    val holes: Int? = null,
    val roundType: RoundType? = null,
    val players: List<Player> = emptyList(),
    val isValid: Boolean = false
)

@HiltViewModel
class CreateRoundViewModel @Inject constructor(
    private val repo: RoundsRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(CreateRoundUi())
    val ui: StateFlow<CreateRoundUi> = _ui.asStateFlow()

    fun setTees(v: TeeColour) = update { it.copy(tees = v) }
    fun setHoles(v: Int) = update { it.copy(holes = v) }
    fun setType(v: RoundType) = update { it.copy(roundType = v) }

    fun addPlayer(p: Player) = update { it.copy(players = it.players + p) }
    fun removePlayer(id: String) =
        update { it.copy(players = it.players.filterNot { p -> p.id == id }) }

    private fun update(f: (CreateRoundUi) -> CreateRoundUi) {
        val next = f(_ui.value).let { it.copy(isValid = validate(it)) }
        _ui.value = next
    }

    private fun validate(u: CreateRoundUi): Boolean =
        u.course.isNotBlank() && u.tees != null && u.holes != null && u.roundType != null && u.players.isNotEmpty()

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
            val card = repo.createRound(meta, s.players)
            onCreated(card.round.id)
        }
    }
}
