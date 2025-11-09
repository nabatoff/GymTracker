package com.gymtracker.data.repository

import com.gymtracker.data.dao.*
import com.gymtracker.data.entities.*
import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val workoutSessionDao: WorkoutSessionDao,
    private val bodyMetricDao: BodyMetricDao,
    private val workoutProgramDao: WorkoutProgramDao,
    private val exerciseDao: ExerciseDao,
    private val goalDao: GoalDao
) {
    // WorkoutSession
    fun getAllSessions(): Flow<List<WorkoutSession>> = workoutSessionDao.getAllSessionsSortedByDate()
    suspend fun getSessionById(id: Long) = workoutSessionDao.getSessionById(id)
    suspend fun insertSession(session: WorkoutSession) = workoutSessionDao.insert(session)
    suspend fun updateSession(session: WorkoutSession) = workoutSessionDao.update(session)
    suspend fun deleteSession(session: WorkoutSession) = workoutSessionDao.delete(session)
    suspend fun getSessionsInRange(startTime: Long, endTime: Long) = 
        workoutSessionDao.getSessionsInRange(startTime, endTime)
    suspend fun getTotalDurationInRange(startTime: Long, endTime: Long) = 
        workoutSessionDao.getTotalDurationInRange(startTime, endTime) ?: 0
    suspend fun getSessionCountInRange(startTime: Long, endTime: Long) = 
        workoutSessionDao.getSessionCountInRange(startTime, endTime)

    // BodyMetric
    fun getAllMetrics(): Flow<List<BodyMetric>> = bodyMetricDao.getAllMetricsSortedByDate()
    suspend fun getMetricById(id: Long) = bodyMetricDao.getMetricById(id)
    suspend fun getLatestMetric() = bodyMetricDao.getLatestMetric()
    suspend fun insertMetric(metric: BodyMetric) = bodyMetricDao.insert(metric)
    suspend fun updateMetric(metric: BodyMetric) = bodyMetricDao.update(metric)
    suspend fun deleteMetric(metric: BodyMetric) = bodyMetricDao.delete(metric)

    // WorkoutProgram
    fun getAllPrograms(): Flow<List<WorkoutProgram>> = workoutProgramDao.getAllPrograms()
    suspend fun getProgramById(id: Long) = workoutProgramDao.getProgramById(id)
    suspend fun insertProgram(program: WorkoutProgram) = workoutProgramDao.insert(program)
    suspend fun updateProgram(program: WorkoutProgram) = workoutProgramDao.update(program)
    suspend fun deleteProgram(program: WorkoutProgram) = workoutProgramDao.delete(program)

    // Exercise
    fun getExercisesForProgram(programId: Long): Flow<List<Exercise>> = 
        exerciseDao.getExercisesForProgram(programId)
    suspend fun getExerciseById(id: Long) = exerciseDao.getExerciseById(id)
    suspend fun insertExercise(exercise: Exercise) = exerciseDao.insert(exercise)
    suspend fun updateExercise(exercise: Exercise) = exerciseDao.update(exercise)
    suspend fun deleteExercise(exercise: Exercise) = exerciseDao.delete(exercise)

    // Goal
    fun getAllGoals(): Flow<List<Goal>> = goalDao.getAllGoals()
    suspend fun getGoalById(id: Long) = goalDao.getGoalById(id)
    suspend fun insertGoal(goal: Goal) = goalDao.insert(goal)
    suspend fun updateGoal(goal: Goal) = goalDao.update(goal)
    suspend fun deleteGoal(goal: Goal) = goalDao.delete(goal)
}
