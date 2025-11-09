package com.gymtracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymtracker.data.entities.BodyMetric
import com.gymtracker.data.entities.Goal
import com.gymtracker.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class ProgressViewModel(private val repository: AppRepository) : ViewModel() {
    val metrics = repository.getAllMetrics()
    val goals = repository.getAllGoals()

    private val _weightInput = MutableStateFlow("")
    val weightInput: StateFlow<String> = _weightInput.asStateFlow()

    private val _bodyFatInput = MutableStateFlow("")
    val bodyFatInput: StateFlow<String> = _bodyFatInput.asStateFlow()

    private val _muscleMassInput = MutableStateFlow("")
    val muscleMassInput: StateFlow<String> = _muscleMassInput.asStateFlow()

    fun updateWeightInput(value: String) {
        _weightInput.value = value
    }

    fun updateBodyFatInput(value: String) {
        _bodyFatInput.value = value
    }

    fun updateMuscleMassInput(value: String) {
        _muscleMassInput.value = value
    }

    fun saveBodyMetric() {
        val weight = _weightInput.value.toFloatOrNull() ?: return
        val bodyFat = _bodyFatInput.value.toFloatOrNull()
        val muscleMass = _muscleMassInput.value.toFloatOrNull()

        viewModelScope.launch {
            repository.insertMetric(
                BodyMetric(
                    date = System.currentTimeMillis(),
                    weight = weight,
                    bodyFatPercentage = bodyFat,
                    muscleMass = muscleMass
                )
            )
            // Очищаем поля после сохранения
            _weightInput.value = ""
            _bodyFatInput.value = ""
            _muscleMassInput.value = ""

            // Обновляем цель по весу
            updateWeightGoal(weight)
        }
    }

    private suspend fun updateWeightGoal(currentWeight: Float) {
        val goals = repository.getAllGoals()
        goals.collect { goalsList ->
            goalsList.find { it.title == "Целевой вес" }?.let { weightGoal ->
                repository.updateGoal(weightGoal.copy(currentValue = currentWeight))
            }
        }
    }

    fun updateGoalsWithCurrentData() {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            val currentMonth = calendar.get(Calendar.MONTH)
            val currentYear = calendar.get(Calendar.YEAR)
            
            // Начало текущего месяца
            calendar.set(currentYear, currentMonth, 1, 0, 0, 0)
            val startOfMonth = calendar.timeInMillis
            
            // Конец текущего месяца
            calendar.set(currentYear, currentMonth, calendar.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59)
            val endOfMonth = calendar.timeInMillis

            // Получаем данные
            val sessionCount = repository.getSessionCountInRange(startOfMonth, endOfMonth)
            val totalDuration = repository.getTotalDurationInRange(startOfMonth, endOfMonth)
            val latestMetric = repository.getLatestMetric()

            // Обновляем цели
            goals.collect { goalsList ->
                goalsList.forEach { goal ->
                    val updatedGoal = when (goal.title) {
                        "Тренировок в месяц" -> goal.copy(currentValue = sessionCount.toFloat())
                        "Целевой вес" -> goal.copy(currentValue = latestMetric?.weight ?: 0f)
                        "Время в зале (в мес.)" -> goal.copy(currentValue = totalDuration.toFloat())
                        else -> goal
                    }
                    repository.updateGoal(updatedGoal)
                }
            }
        }
    }
}
