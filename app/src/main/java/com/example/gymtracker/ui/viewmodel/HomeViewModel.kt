package com.example.gymtracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtracker.data.entity.WorkoutSession
import com.example.gymtracker.data.repository.AppRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WorkoutTimerState(
    val isActive: Boolean = false,
    val startTime: Long? = null,
    val elapsedSeconds: Long = 0
)

class HomeViewModel(private val repository: AppRepository) : ViewModel() {
    private val _timerState = MutableStateFlow(WorkoutTimerState())
    val timerState: StateFlow<WorkoutTimerState> = _timerState.asStateFlow()

    private var timerJob: Job? = null

    fun startWorkout() {
        if (_timerState.value.isActive) return

        val startTime = System.currentTimeMillis()
        _timerState.value = WorkoutTimerState(
            isActive = true,
            startTime = startTime,
            elapsedSeconds = 0
        )

        timerJob = viewModelScope.launch {
            while (_timerState.value.isActive) {
                delay(1000)
                val elapsed = (System.currentTimeMillis() - startTime) / 1000
                _timerState.value = _timerState.value.copy(elapsedSeconds = elapsed)
            }
        }
    }

    fun endWorkout() {
        if (!_timerState.value.isActive) return

        timerJob?.cancel()
        val currentState = _timerState.value
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
        }

        _timerState.value = WorkoutTimerState()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
