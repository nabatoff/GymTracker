package com.example.gymtracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtracker.data.local.entity.WorkoutSession
import com.example.gymtracker.data.repository.AppRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.max
import kotlin.math.roundToInt

data class HomeUiState(
    val isSessionActive: Boolean = false,
    val elapsedMillis: Long = 0L,
    val startTime: Long? = null
)

class HomeViewModel(private val repository: AppRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    fun startWorkout() {
        if (_uiState.value.isSessionActive) return
        val start = System.currentTimeMillis()
        _uiState.value = HomeUiState(
            isSessionActive = true,
            elapsedMillis = 0L,
            startTime = start
        )
        startTimer()
    }

    fun endWorkout() {
        val state = _uiState.value
        val startTime = state.startTime ?: return
        val endTime = System.currentTimeMillis()
        val durationMinutes = max(1, ((endTime - startTime) / 60000.0).roundToInt())

        viewModelScope.launch {
            repository.insertWorkoutSession(
                WorkoutSession(
                    startTime = startTime,
                    endTime = endTime,
                    durationInMinutes = durationMinutes
                )
            )
        }

        timerJob?.cancel()
        _uiState.value = HomeUiState()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000L)
                val startTime = _uiState.value.startTime ?: break
                val elapsed = System.currentTimeMillis() - startTime
                _uiState.update { it.copy(elapsedMillis = elapsed) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
