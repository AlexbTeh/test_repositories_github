package com.done.repositories


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.done.domain.models.Scorecard
import com.done.domain.usecase.ObserveScorecardUseCase
import com.done.domain.usecase.SetStrokeUseCase
import com.done.domain.usecase.SubmitOfflineUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScoreboardViewModel @Inject constructor(
    private val observeScorecard: ObserveScorecardUseCase,
    private val setStroke: SetStrokeUseCase,
    private val submitOffline: SubmitOfflineUseCase
) : ViewModel() {

    fun flow(roundId: String): Flow<Scorecard> = observeScorecard(roundId)

    fun set(roundId: String, playerId: String, hole: Int, strokes: Int?) = viewModelScope.launch {
        setStroke(roundId, playerId, hole, strokes)
    }

    fun submit(roundId: String) = viewModelScope.launch {
        submitOffline(roundId)
    }
}
