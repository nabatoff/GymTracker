package com.example.gymtracker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtracker.data.entities.WorkoutSession
import com.example.gymtracker.data.repository.AppRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val isWorkoutActive: Boolean = false,
    val startTime: Long? = null,
    val elapsedSeconds: Long = 0,
    val currentSession: WorkoutSession? = null
)

class HomeViewModel(private val repository: AppRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    fun startWorkout() {
        val currentTime = System.currentTimeMillis()
        _uiState.value = _uiState.value.copy(
            isWorkoutActive = true,
            startTime = currentTime,
            elapsedSeconds = 0
        )
        startTimer()
    }

    fun endWorkout() {
        timerJob?.cancel()
        val currentState = _uiState.value
        val endTime = System.currentTimeMillis()
        val startTime = currentState.startTime ?: return

        val durationInMinutes = ((endTime - startTime) / 1000 / 60).toInt()

        viewModelScope.launch {
            val session = WorkoutSession(
                startTime = startTime,
                endTime = endTime,
                durationInMinutes = durationInMinutes
            )
            repository.insertSession(session)
            
            _uiState.value = HomeUiState()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.isWorkoutActive) {
                delay(1000)
                val startTime = _uiState.value.startTime ?: break
                val elapsed = (System.currentTimeMillis() - startTime) / 1000
                _uiState.value = _uiState.value.copy(elapsedSeconds = elapsed)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
