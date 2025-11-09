package com.example.gymtracker.data.repository

import com.example.gymtracker.data.local.dao.BodyMetricDao
import com.example.gymtracker.data.local.dao.GoalDao
import com.example.gymtracker.data.local.dao.WorkoutProgramDao
import com.example.gymtracker.data.local.dao.WorkoutSessionDao
import com.example.gymtracker.data.local.entities.BodyMetric
import com.example.gymtracker.data.local.entities.Exercise
import com.example.gymtracker.data.local.entities.Goal
import com.example.gymtracker.data.local.entities.ProgramWithExercises
import com.example.gymtracker.data.local.entities.WorkoutProgram
import com.example.gymtracker.data.local.entities.WorkoutSession
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

data class GoalProgress(
    val goal: Goal,
    val currentValue: Float,
    val targetValue: Float
)

class AppRepository(
    private val workoutSessionDao: WorkoutSessionDao,
    private val bodyMetricDao: BodyMetricDao,
    private val workoutProgramDao: WorkoutProgramDao,
    private val goalDao: GoalDao
) {

    val workoutSessions: Flow<List<WorkoutSession>> = workoutSessionDao.getAllSessionsSortedByDate()
    val bodyMetrics: Flow<List<BodyMetric>> = bodyMetricDao.getAllMetricsSortedByDate()
    val programs: Flow<List<ProgramWithExercises>> = workoutProgramDao.getProgramsWithExercises()
    val goals: Flow<List<Goal>> = goalDao.observeGoals()

    // Workout sessions
    suspend fun insertWorkoutSession(session: WorkoutSession): Long =
        workoutSessionDao.insert(session)

    suspend fun deleteWorkoutSession(session: WorkoutSession) =
        workoutSessionDao.delete(session)

    // Body metrics
    suspend fun insertBodyMetric(metric: BodyMetric): Long =
        bodyMetricDao.insert(metric)

    suspend fun deleteBodyMetric(metric: BodyMetric) =
        bodyMetricDao.delete(metric)

    suspend fun getLatestBodyMetric(): BodyMetric? =
        bodyMetricDao.getLatestMetric()

    // Programs and exercises
    suspend fun addProgram(program: WorkoutProgram): Long =
        workoutProgramDao.insertProgram(program)

    suspend fun updateProgram(program: WorkoutProgram) =
        workoutProgramDao.updateProgram(program)

    suspend fun deleteProgram(program: WorkoutProgram) =
        workoutProgramDao.deleteProgram(program)

    fun observeProgram(programId: Long): Flow<ProgramWithExercises?> =
        workoutProgramDao.observeProgramWithExercises(programId)

    suspend fun addExercise(exercise: Exercise): Long =
        workoutProgramDao.insertExercise(exercise)

    suspend fun updateExercise(exercise: Exercise) =
        workoutProgramDao.updateExercise(exercise)

    suspend fun deleteExercise(exercise: Exercise) =
        workoutProgramDao.deleteExercise(exercise)

    // Goals
    suspend fun upsertGoal(goal: Goal) {
        if (goal.goalId == 0L) {
            goalDao.insert(goal)
        } else {
            goalDao.update(goal)
        }
    }

    suspend fun ensureDefaultGoals() {
        if (goalDao.getGoalsOnce().isNotEmpty()) return
        val defaults = listOf(
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
        goalDao.insertAll(defaults)
    }

    suspend fun buildGoalProgress(): List<GoalProgress> {
        val goals = goalDao.getGoalsOnce()
        if (goals.isEmpty()) return emptyList()

        val zoneId = ZoneId.systemDefault()
        val firstDayOfMonth = LocalDate.now(zoneId).with(TemporalAdjusters.firstDayOfMonth())
        val startOfMonth = firstDayOfMonth.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val endOfMonth = firstDayOfMonth
            .with(TemporalAdjusters.lastDayOfMonth())
            .plusDays(1)
            .atStartOfDay(zoneId)
            .minusSeconds(1)
            .toInstant()
            .toEpochMilli()

        val sessionsThisMonth = workoutSessionDao.countSessionsBetween(startOfMonth, endOfMonth)
        val minutesThisMonth = workoutSessionDao.sumDurationBetween(startOfMonth, endOfMonth)
        val latestWeight = bodyMetricDao.getLatestMetric()?.weight ?: 0f

        return goals.map { goal ->
            val current = when (goal.title) {
                "Тренировок в месяц" -> sessionsThisMonth.toFloat()
                "Целевой вес" -> latestWeight
                "Время в зале (в мес.)" -> minutesThisMonth.toFloat()
                else -> goal.currentValue
            }
            GoalProgress(
                goal = goal.copy(currentValue = current),
                currentValue = current,
                targetValue = goal.targetValue
            )
        }
    }

    suspend fun countSessionsThisMonth(): Int {
        val zoneId = ZoneId.systemDefault()
        val firstDay = LocalDate.now(zoneId).with(TemporalAdjusters.firstDayOfMonth())
        val start = firstDay.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val end = firstDay.with(TemporalAdjusters.lastDayOfMonth())
            .plusDays(1).atStartOfDay(zoneId).minusSeconds(1).toInstant().toEpochMilli()
        return workoutSessionDao.countSessionsBetween(start, end)
    }

    suspend fun sumDurationThisMonth(): Int {
        val zoneId = ZoneId.systemDefault()
        val firstDay = LocalDate.now(zoneId).with(TemporalAdjusters.firstDayOfMonth())
        val start = firstDay.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val end = firstDay.with(TemporalAdjusters.lastDayOfMonth())
            .plusDays(1).atStartOfDay(zoneId).minusSeconds(1).toInstant().toEpochMilli()
        return workoutSessionDao.sumDurationBetween(start, end)
    }
}
