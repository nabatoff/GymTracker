package com.example.gymtracker.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtracker.data.local.entities.BodyMetric
import com.example.gymtracker.data.repository.AppRepository
import com.example.gymtracker.data.repository.GoalProgress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProgressUiState(
    val metrics: List<BodyMetric> = emptyList(),
    val goals: List<GoalProgress> = emptyList(),
    val isSaving: Boolean = false
)

class ProgressViewModel(
    private val repository: AppRepository
) : ViewModel() {

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    val metrics: StateFlow<List<BodyMetric>> = repository.bodyMetrics
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val _goalProgress = MutableStateFlow<List<GoalProgress>>(emptyList())
    val goalProgress: StateFlow<List<GoalProgress>> = _goalProgress.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.bodyMetrics,
                repository.workoutSessions,
                repository.goals
            ) { _, _, _ ->
                repository.buildGoalProgress()
            }.collect { progress ->
                _goalProgress.value = progress
            }
        }
    }

    fun saveMetric(
        weight: Float,
        bodyFat: Float?,
        muscleMass: Float?
    ) {
        if (weight <= 0f) return
        viewModelScope.launch {
            _isSaving.value = true
            repository.insertBodyMetric(
                BodyMetric(
                    date = System.currentTimeMillis(),
                    weight = weight,
                    bodyFatPercentage = bodyFat,
                    muscleMass = muscleMass
                )
            )
            _isSaving.value = false
        }
    }
}
