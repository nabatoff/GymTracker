package com.example.gymtracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtracker.data.entity.BodyMetric
import com.example.gymtracker.data.entity.Goal
import com.example.gymtracker.data.repository.AppRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProgressViewModel(private val repository: AppRepository) : ViewModel() {
    val bodyMetrics = repository.getAllMetrics()
    val goals = repository.getAllGoals()

    private val _weightData = MutableStateFlow<List<Pair<Long, Float>>>(emptyList())
    val weightData: StateFlow<List<Pair<Long, Float>>> = _weightData.asStateFlow()

    init {
        viewModelScope.launch {
            bodyMetrics.collect { metrics ->
                _weightData.value = metrics.map { it.date to it.weight }
            }
        }
        initializeDefaultGoals()
    }

    fun saveBodyMetric(weight: Float, bodyFat: Float?, muscleMass: Float?) {
        viewModelScope.launch {
            val metric = BodyMetric(
                date = System.currentTimeMillis(),
                weight = weight,
                bodyFatPercentage = bodyFat,
                muscleMass = muscleMass
            )
            repository.insertMetric(metric)
            updateGoals()
        }
    }

    suspend fun getLatestWeight(): Float? {
        return repository.getLatestMetric()?.weight
    }

    suspend fun getSessionsCountThisMonth(): Int {
        val startOfMonth = repository.getCurrentMonthStart()
        val endOfMonth = repository.getCurrentMonthEnd()
        return repository.getSessionsCountForMonth(startOfMonth, endOfMonth)
    }

    suspend fun getTotalDurationThisMonth(): Int {
        val startOfMonth = repository.getCurrentMonthStart()
        val endOfMonth = repository.getCurrentMonthEnd()
        return repository.getTotalDurationForMonth(startOfMonth, endOfMonth) ?: 0
    }

    private fun updateGoals() {
        viewModelScope.launch {
            goals.first().let { allGoals ->
                for (goal in allGoals) {
                    val updatedCurrentValue = when (goal.title) {
                        "Тренировок в месяц" -> getSessionsCountThisMonth().toFloat()
                        "Целевой вес" -> getLatestWeight() ?: goal.currentValue
                        "Время в зале (в мес.)" -> getTotalDurationThisMonth().toFloat()
                        else -> goal.currentValue
                    }
                    if (updatedCurrentValue != goal.currentValue) {
                        repository.updateGoal(goal.copy(currentValue = updatedCurrentValue))
                    }
                }
            }
        }
    }

    private fun initializeDefaultGoals() {
        viewModelScope.launch {
            val existingGoals = goals.first()
            if (existingGoals.isEmpty()) {
                val defaultGoals = listOf(
                    Goal(
                        title = "Тренировок в месяц",
                        targetValue = 12f,
                        currentValue = 0f,
                        unit = "тренировок"
                    ),
                    Goal(
                        title = "Целевой вес",
                        targetValue = 80f,
                        currentValue = 0f,
                        unit = "кг"
                    ),
                    Goal(
                        title = "Время в зале (в мес.)",
                        targetValue = 1000f,
                        currentValue = 0f,
                        unit = "минут"
                    )
                )
                defaultGoals.forEach { repository.insertGoal(it) }
            }
            updateGoals()
        }
    }
}
