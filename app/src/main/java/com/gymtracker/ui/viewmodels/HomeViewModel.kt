package com.gymtracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymtracker.data.entities.WorkoutSession
import com.gymtracker.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.concurrent.fixedRateTimer

class HomeViewModel(private val repository: AppRepository) : ViewModel() {
    private val _isWorkoutActive = MutableStateFlow(false)
    val isWorkoutActive: StateFlow<Boolean> = _isWorkoutActive.asStateFlow()

    private val _workoutStartTime = MutableStateFlow<Long?>(null)
    val workoutStartTime: StateFlow<Long?> = _workoutStartTime.asStateFlow()

    private val _elapsedSeconds = MutableStateFlow(0)
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds.asStateFlow()

    private var timer: java.util.Timer? = null
    private var currentSessionId: Long? = null

    fun startWorkout() {
        val startTime = System.currentTimeMillis()
        _workoutStartTime.value = startTime
        _isWorkoutActive.value = true
        _elapsedSeconds.value = 0

        // Сохраняем сессию в базу
        viewModelScope.launch {
            currentSessionId = repository.insertSession(
                WorkoutSession(
                    startTime = startTime,
                    endTime = 0,
                    durationInMinutes = 0
                )
            )
        }

        // Запускаем таймер
        timer = fixedRateTimer(period = 1000) {
            _elapsedSeconds.value += 1
        }
    }

    fun endWorkout() {
        timer?.cancel()
        timer = null

        val endTime = System.currentTimeMillis()
        val startTime = _workoutStartTime.value ?: return
        val durationInMinutes = ((endTime - startTime) / 1000 / 60).toInt()

        // Обновляем сессию в базе
        viewModelScope.launch {
            currentSessionId?.let { id ->
                val session = repository.getSessionById(id)
                session?.let {
                    repository.updateSession(
                        it.copy(
                            endTime = endTime,
                            durationInMinutes = durationInMinutes
                        )
                    )
                }
            }
        }

        _isWorkoutActive.value = false
        _workoutStartTime.value = null
        _elapsedSeconds.value = 0
        currentSessionId = null
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }

    fun formatElapsedTime(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, secs)
    }
}
