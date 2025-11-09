package com.example.gymtracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtracker.data.entity.WorkoutSession
import com.example.gymtracker.data.repository.AppRepository
import com.example.gymtracker.ui.home.HomeStats
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class WorkoutTimerState(
    val isActive: Boolean = false,
    val startTime: Long? = null,
    val elapsedSeconds: Long = 0
)

class HomeViewModel(private val repository: AppRepository) : ViewModel() {
    private val _timerState = MutableStateFlow(WorkoutTimerState())
    val timerState: StateFlow<WorkoutTimerState> = _timerState.asStateFlow()

    val stats: StateFlow<HomeStats> = combine(
        repository.getAllSessions(),
        repository.getAllMetrics()
    ) { sessions, metrics ->
        val totalWorkouts = sessions.size
        
        // Вычисляем тренировки за месяц локально
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfMonth = calendar.timeInMillis
        
        calendar.add(Calendar.MONTH, 1)
        val endOfMonth = calendar.timeInMillis
        
        val workoutsThisMonth = sessions.count { 
            it.startTime >= startOfMonth && it.startTime < endOfMonth 
        }
        
        val totalMinutes = sessions.sumOf { it.durationInMinutes?.toLong() ?: 0L }.toInt()
        val currentWeight = metrics.firstOrNull()?.weight
        val lastWorkoutDate = sessions.firstOrNull()?.let {
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(it.startTime))
        }
        
        HomeStats(
            totalWorkouts = totalWorkouts,
            workoutsThisMonth = workoutsThisMonth,
            totalMinutes = totalMinutes,
            currentWeight = currentWeight,
            lastWorkoutDate = lastWorkoutDate
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeStats()
    )

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
            // Обновляем статистику после сохранения
        }

        _timerState.value = WorkoutTimerState()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
