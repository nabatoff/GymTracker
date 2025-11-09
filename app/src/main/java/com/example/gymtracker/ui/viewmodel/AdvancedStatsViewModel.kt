package com.example.gymtracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtracker.data.repository.AppRepository
import kotlinx.coroutines.flow.*
import java.util.*

data class AdvancedStats(
    val avgWorkoutDuration: Int = 0,
    val totalHours: Int = 0,
    val workoutsPerWeek: Float = 0f,
    val weightChange: Float = 0f,
    val currentMonthWorkouts: Int = 0,
    val previousMonthWorkouts: Int = 0
)

class AdvancedStatsViewModel(private val repository: AppRepository) : ViewModel() {
    
    val stats: StateFlow<AdvancedStats> = combine(
        repository.getAllSessions(),
        repository.getAllMetrics()
    ) { sessions, metrics ->
        val calendar = Calendar.getInstance()
        
        // Текущий месяц
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfCurrentMonth = calendar.timeInMillis
        
        calendar.add(Calendar.MONTH, 1)
        val endOfCurrentMonth = calendar.timeInMillis
        
        // Прошлый месяц
        calendar.add(Calendar.MONTH, -2)
        val startOfPreviousMonth = calendar.timeInMillis
        
        val currentMonthSessions = sessions.filter { 
            it.startTime >= startOfCurrentMonth && it.startTime < endOfCurrentMonth 
        }
        val previousMonthSessions = sessions.filter { 
            it.startTime >= startOfPreviousMonth && it.startTime < startOfCurrentMonth 
        }
        
        val durations = sessions.mapNotNull { it.durationInMinutes }
        val avgDuration = if (durations.isNotEmpty()) {
            durations.average().toInt()
        } else 0
        
        val totalMinutes = durations.sum()
        val totalHours = totalMinutes / 60
        
        // Тренировок в неделю (за последние 4 недели)
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(Calendar.WEEK_OF_YEAR, -4)
        val fourWeeksAgo = calendar.timeInMillis
        val recentSessions = sessions.filter { it.startTime >= fourWeeksAgo }
        val workoutsPerWeek = if (recentSessions.isNotEmpty()) {
            recentSessions.size / 4f
        } else 0f
        
        // Изменение веса за месяц
        val currentMonthMetrics = metrics.filter { 
            it.date >= startOfCurrentMonth && it.date < endOfCurrentMonth 
        }
        val previousMonthMetrics = metrics.filter { 
            it.date >= startOfPreviousMonth && it.date < startOfCurrentMonth 
        }
        val weightChange = if (currentMonthMetrics.isNotEmpty() && previousMonthMetrics.isNotEmpty()) {
            currentMonthMetrics.first().weight - previousMonthMetrics.first().weight
        } else 0f
        
        AdvancedStats(
            avgWorkoutDuration = avgDuration,
            totalHours = totalHours,
            workoutsPerWeek = workoutsPerWeek,
            weightChange = weightChange,
            currentMonthWorkouts = currentMonthSessions.size,
            previousMonthWorkouts = previousMonthSessions.size
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AdvancedStats()
    )
}
