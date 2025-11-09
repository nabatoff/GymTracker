package com.example.gymtracker.data.repository

import com.example.gymtracker.data.local.dao.BodyMetricDao
import com.example.gymtracker.data.local.dao.GoalDao
import com.example.gymtracker.data.local.dao.WorkoutProgramDao
import com.example.gymtracker.data.local.dao.WorkoutSessionDao
import com.example.gymtracker.data.local.entity.BodyMetric
import com.example.gymtracker.data.local.entity.Exercise
import com.example.gymtracker.data.local.entity.Goal
import com.example.gymtracker.data.local.entity.ProgramWithExercises
import com.example.gymtracker.data.local.entity.WorkoutProgram
import com.example.gymtracker.data.local.entity.WorkoutSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AppRepository(
    private val workoutSessionDao: WorkoutSessionDao,
    private val bodyMetricDao: BodyMetricDao,
    private val workoutProgramDao: WorkoutProgramDao,
    private val goalDao: GoalDao
) {

    // Workout Sessions
    fun getAllWorkoutSessionsSorted(): Flow<List<WorkoutSession>> =
        workoutSessionDao.getAllSessionsSortedByDate()

    fun getAllWorkoutSessions(): Flow<List<WorkoutSession>> =
        workoutSessionDao.getAllSessions()

    suspend fun insertWorkoutSession(session: WorkoutSession) {
        workoutSessionDao.insert(session)
    }

    suspend fun updateWorkoutSession(session: WorkoutSession) {
        workoutSessionDao.update(session)
    }

    suspend fun deleteWorkoutSession(session: WorkoutSession) {
        workoutSessionDao.delete(session)
    }

    suspend fun getSessionsBetween(start: Long, end: Long): List<WorkoutSession> =
        workoutSessionDao.getSessionsBetween(start, end)

    // Body Metrics
    fun getBodyMetricsSorted(): Flow<List<BodyMetric>> =
        bodyMetricDao.getAllMetricsSortedByDate()

    fun getBodyMetrics(): Flow<List<BodyMetric>> =
        bodyMetricDao.getAllMetrics()

    suspend fun insertBodyMetric(metric: BodyMetric) {
        bodyMetricDao.insert(metric)
    }

    suspend fun updateBodyMetric(metric: BodyMetric) {
        bodyMetricDao.update(metric)
    }

    suspend fun deleteBodyMetric(metric: BodyMetric) {
        bodyMetricDao.delete(metric)
    }

    suspend fun getLatestMetric(): BodyMetric? = bodyMetricDao.getLatestMetric()

    // Workout Programs & Exercises
    fun getAllPrograms(): Flow<List<WorkoutProgram>> = workoutProgramDao.getAllPrograms()

    fun getAllProgramsWithExercises(): Flow<List<ProgramWithExercises>> =
        workoutProgramDao.getAllProgramsWithExercises()

    fun getProgramWithExercises(programId: Long): Flow<ProgramWithExercises?> =
        workoutProgramDao.getProgramWithExercises(programId)

    suspend fun insertProgram(program: WorkoutProgram): Long =
        workoutProgramDao.insertProgram(program)

    suspend fun updateProgram(program: WorkoutProgram) {
        workoutProgramDao.updateProgram(program)
    }

    suspend fun deleteProgram(program: WorkoutProgram) {
        workoutProgramDao.deleteProgram(program)
    }

    suspend fun insertExercise(exercise: Exercise): Long =
        workoutProgramDao.insertExercise(exercise)

    suspend fun updateExercise(exercise: Exercise) {
        workoutProgramDao.updateExercise(exercise)
    }

    suspend fun deleteExercise(exercise: Exercise) {
        workoutProgramDao.deleteExercise(exercise)
    }

    fun getExercisesForProgram(programId: Long) = workoutProgramDao.getExercisesForProgram(programId)

    // Goals
    fun getGoals(): Flow<List<Goal>> = goalDao.getGoals()

    suspend fun insertGoal(goal: Goal) {
        goalDao.insert(goal)
    }

    suspend fun insertGoals(goals: List<Goal>) {
        goalDao.insertAll(goals)
    }

    suspend fun updateGoal(goal: Goal) {
        goalDao.update(goal)
    }

    suspend fun ensureDefaultGoals(defaultGoals: List<Goal>) {
        withContext(Dispatchers.IO) {
            if (goalDao.count() == 0) {
                goalDao.insertAll(defaultGoals)
            }
        }
    }
}
