package com.example.gymtracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtracker.data.local.entity.BodyMetric
import com.example.gymtracker.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters

data class ProgressFormState(
    val weight: String = "",
    val bodyFat: String = "",
    val muscleMass: String = ""
)

data class GoalProgress(
    val id: Long,
    val title: String,
    val targetValue: Float,
    val currentValue: Float,
    val unit: String,
    val progress: Float
)

data class ProgressUiState(
    val metrics: List<BodyMetric> = emptyList(),
    val goals: List<GoalProgress> = emptyList(),
    val weightEntries: List<Pair<Long, Float>> = emptyList(),
    val latestWeight: Float? = null
)

class ProgressViewModel(private val repository: AppRepository) : ViewModel() {

    private val formStateMutable = MutableStateFlow(ProgressFormState())
    val formState: StateFlow<ProgressFormState> = formStateMutable

    private val metricsFlow = repository.getBodyMetricsSorted()
    private val goalsFlow = repository.getGoals()
    private val sessionsFlow = repository.getAllWorkoutSessions()

    val uiState: StateFlow<ProgressUiState> = combine(
        metricsFlow,
        goalsFlow,
        sessionsFlow
    ) { metricsDescending, goals, sessions ->
        val metrics = metricsDescending.sortedBy { it.date }
        val weightEntries = metrics.map { it.date to it.weight }
        val latestWeight = metricsDescending.firstOrNull()?.weight

        val zone = ZoneId.systemDefault()
        val now = ZonedDateTime.now(zone)
        val startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth())
            .withHour(0)
            .withMinute(0)
            .withSecond(0)
            .withNano(0)
            .toInstant()
            .toEpochMilli()
        val endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth())
            .withHour(23)
            .withMinute(59)
            .withSecond(59)
            .withNano(999_000_000)
            .toInstant()
            .toEpochMilli()

        val sessionsThisMonth = sessions.filter { it.startTime in startOfMonth..endOfMonth }
        val workoutsCount = sessionsThisMonth.size
        val totalMinutes = sessionsThisMonth.sumOf { it.durationInMinutes }

        val goalProgress = goals.map { goal ->
            val dynamicCurrent = when (goal.title) {
                "Тренировок в месяц" -> workoutsCount.toFloat()
                "Целевой вес" -> latestWeight ?: goal.currentValue
                "Время в зале (в мес.)" -> totalMinutes.toFloat()
                else -> goal.currentValue
            }
            val progress = if (goal.targetValue == 0f) 0f else (dynamicCurrent / goal.targetValue).coerceIn(0f, 1f)
            GoalProgress(
                id = goal.goalId,
                title = goal.title,
                targetValue = goal.targetValue,
                currentValue = dynamicCurrent,
                unit = goal.unit,
                progress = progress
            )
        }

        ProgressUiState(
            metrics = metrics,
            goals = goalProgress,
            weightEntries = weightEntries,
            latestWeight = latestWeight
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProgressUiState()
    )

    fun onWeightChange(value: String) = formStateMutable.update { it.copy(weight = value) }

    fun onBodyFatChange(value: String) = formStateMutable.update { it.copy(bodyFat = value) }

    fun onMuscleMassChange(value: String) = formStateMutable.update { it.copy(muscleMass = value) }

    fun saveMetric() {
        val weight = formState.value.weight.replace(',', '.').toFloatOrNull() ?: return
        val bodyFat = formState.value.bodyFat.replace(',', '.').toFloatOrNull()
        val muscleMass = formState.value.muscleMass.replace(',', '.').toFloatOrNull()

        viewModelScope.launch {
            repository.insertBodyMetric(
                BodyMetric(
                    date = System.currentTimeMillis(),
                    weight = weight,
                    bodyFatPercentage = bodyFat,
                    muscleMass = muscleMass
                )
            )
            formStateMutable.value = ProgressFormState()
        }
    }
}
