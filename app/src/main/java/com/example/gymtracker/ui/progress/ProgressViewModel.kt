package com.example.gymtracker.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtracker.data.entities.BodyMetric
import com.example.gymtracker.data.entities.Goal
import com.example.gymtracker.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

data class ProgressUiState(
    val metrics: List<BodyMetric> = emptyList(),
    val goals: List<Goal> = emptyList(),
    val isLoading: Boolean = false
)

class ProgressViewModel(private val repository: AppRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    init {
        loadData()
        initializeDefaultGoals()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                repository.getAllMetrics(),
                repository.getAllGoals()
            ) { metrics, goals ->
                Pair(metrics, goals)
            }.collect { (metrics, goals) ->
                _uiState.value = _uiState.value.copy(
                    metrics = metrics,
                    goals = updateGoalsCurrentValues(goals, metrics)
                )
            }
        }
    }

    private suspend fun updateGoalsCurrentValues(
        goals: List<Goal>,
        metrics: List<BodyMetric>
    ): List<Goal> {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)
        
        calendar.set(currentYear, currentMonth, 1, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfMonth = calendar.timeInMillis
        
        calendar.add(Calendar.MONTH, 1)
        val endOfMonth = calendar.timeInMillis

        return goals.map { goal ->
            val currentValue = when (goal.title) {
                "Тренировок в месяц" -> {
                    repository.getSessionsCountInMonth(startOfMonth, endOfMonth).toFloat()
                }
                "Целевой вес" -> {
                    repository.getLatestMetric()?.weight ?: 0f
                }
                "Время в зале (в мес.)" -> {
                    repository.getTotalDurationInMonth(startOfMonth, endOfMonth).toFloat()
                }
                else -> goal.currentValue
            }
            goal.copy(currentValue = currentValue)
        }
    }

    private fun initializeDefaultGoals() {
        viewModelScope.launch {
            val goalsList = repository.getAllGoals().first()
            if (goalsList.isEmpty()) {
                repository.insertGoal(
                    Goal(
                        title = "Тренировок в месяц",
                        targetValue = 12f,
                        currentValue = 0f,
                        unit = "тренировок"
                    )
                )
                repository.insertGoal(
                    Goal(
                        title = "Целевой вес",
                        targetValue = 80f,
                        currentValue = 0f,
                        unit = "кг"
                    )
                )
                repository.insertGoal(
                    Goal(
                        title = "Время в зале (в мес.)",
                        targetValue = 1000f,
                        currentValue = 0f,
                        unit = "минут"
                    )
                )
            }
        }
    }

    fun saveBodyMetric(weight: Float, bodyFat: Float?, muscleMass: Float?) {
        viewModelScope.launch {
            repository.insertMetric(
                BodyMetric(
                    date = System.currentTimeMillis(),
                    weight = weight,
                    bodyFatPercentage = bodyFat,
                    muscleMass = muscleMass
                )
            )
        }
    }
}
