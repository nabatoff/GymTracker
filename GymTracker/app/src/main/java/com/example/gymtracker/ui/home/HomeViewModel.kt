package com.example.gymtracker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtracker.data.local.entities.WorkoutSession
import com.example.gymtracker.data.repository.AppRepository
import com.example.gymtracker.util.formatDuration
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

data class HomeUiState(
    val isSessionActive: Boolean = false,
    val startTimeMillis: Long? = null,
    val elapsedSeconds: Long = 0,
    val isSaving: Boolean = false
) {
    val formattedDuration: String
        get() = formatDuration(elapsedSeconds)
}

class HomeViewModel(
    private val repository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    fun startWorkout() {
        if (_uiState.value.isSessionActive) return
        val startTime = System.currentTimeMillis()
        _uiState.update {
            it.copy(
                isSessionActive = true,
                startTimeMillis = startTime,
                elapsedSeconds = 0,
                isSaving = false
            )
        }
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1_000)
                _uiState.update { state ->
                    val elapsed = ((System.currentTimeMillis() - (state.startTimeMillis ?: startTime)) / 1000)
                    state.copy(elapsedSeconds = elapsed.coerceAtLeast(0))
                }
            }
        }
    }

    fun stopWorkout() {
        val state = _uiState.value
        if (!state.isSessionActive || state.startTimeMillis == null) return
        timerJob?.cancel()
        timerJob = null

        val endTime = System.currentTimeMillis()
        val durationMillis = endTime - state.startTimeMillis
        val durationMinutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis).toInt().coerceAtLeast(1)

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            repository.insertWorkoutSession(
                WorkoutSession(
                    startTime = state.startTimeMillis,
                    endTime = endTime,
                    durationInMinutes = durationMinutes
                )
            )
            _uiState.update { HomeUiState() }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
