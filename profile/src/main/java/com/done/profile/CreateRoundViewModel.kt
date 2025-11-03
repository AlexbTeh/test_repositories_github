package com.done.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.done.common.Time
import com.done.domain.models.Player
import com.done.domain.models.RoundMeta
import com.done.domain.models.RoundType
import com.done.domain.models.TeeColour
import com.done.domain.usecase.CreateRoundUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreateUiState(
    val tee: TeeColour? = TeeColour.RED,
    val holes: Int? = 18,
    val type: RoundType? = RoundType.STABLEFORD,
    val players: List<Player> = emptyList()
)

@HiltViewModel
class CreateRoundViewModel @Inject constructor(
    private val createRound: CreateRoundUseCase
) : ViewModel() {
    private val _ui = MutableStateFlow(CreateUiState(players = listOf(Player(name = "John Smith"))))
    val ui = _ui.asStateFlow()

    fun setTee(t: TeeColour) = _ui.update { it.copy(tee = t) }
    fun setHoles(h: Int) = _ui.update { it.copy(holes = h) }
    fun setType(t: RoundType) = _ui.update { it.copy(type = t) }

    fun addPlayerDialog() {
        val idx = _ui.value.players.size + 1
        addPlayer("John Smith $idx")
    }
    fun addPlayer(name: String) = _ui.update { it.copy(players = it.players + Player(name = name)) }
    fun removePlayer(id: String) = _ui.update { it.copy(players = it.players.filterNot { p -> p.id == id }) }

    fun start(onStart: (String) -> Unit) = viewModelScope.launch {
        val s = _ui.value
        val meta = RoundMeta(
            course = "Cronulla Golf club",
            date = Time.today(),
            tee = s.tee!!, holes = s.holes!!, type = s.type!!
        )
        val sc = createRound(meta, s.players)
        onStart(sc.round.id)
    }
}
