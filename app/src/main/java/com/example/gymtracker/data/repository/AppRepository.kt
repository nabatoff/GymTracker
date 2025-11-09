package com.example.gymtracker.data.repository

import com.example.gymtracker.data.dao.*
import com.example.gymtracker.data.entities.*
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
    
    suspend fun getSessionById(id: Long): WorkoutSession? = workoutSessionDao.getSessionById(id)
    
    suspend fun insertSession(session: WorkoutSession): Long = workoutSessionDao.insertSession(session)
    
    suspend fun updateSession(session: WorkoutSession) = workoutSessionDao.updateSession(session)
    
    suspend fun deleteSession(session: WorkoutSession) = workoutSessionDao.deleteSession(session)
    
    suspend fun getSessionsCountInMonth(startOfMonth: Long, endOfMonth: Long): Int =
        workoutSessionDao.getSessionsCountInMonth(startOfMonth, endOfMonth)
    
    suspend fun getTotalDurationInMonth(startOfMonth: Long, endOfMonth: Long): Int =
        workoutSessionDao.getTotalDurationInMonth(startOfMonth, endOfMonth)

    // BodyMetric
    fun getAllMetrics(): Flow<List<BodyMetric>> = bodyMetricDao.getAllMetricsSortedByDate()
    
    suspend fun getLatestMetric(): BodyMetric? = bodyMetricDao.getLatestMetric()
    
    suspend fun insertMetric(metric: BodyMetric): Long = bodyMetricDao.insertMetric(metric)
    
    suspend fun updateMetric(metric: BodyMetric) = bodyMetricDao.updateMetric(metric)
    
    suspend fun deleteMetric(metric: BodyMetric) = bodyMetricDao.deleteMetric(metric)

    // WorkoutProgram
    fun getAllPrograms(): Flow<List<WorkoutProgram>> = workoutProgramDao.getAllPrograms()
    
    suspend fun getProgramById(programId: Long): WorkoutProgram? = workoutProgramDao.getProgramById(programId)
    
    suspend fun insertProgram(program: WorkoutProgram): Long = workoutProgramDao.insertProgram(program)
    
    suspend fun updateProgram(program: WorkoutProgram) = workoutProgramDao.updateProgram(program)
    
    suspend fun deleteProgram(program: WorkoutProgram) = workoutProgramDao.deleteProgram(program)

    // Exercise
    fun getExercisesByProgramId(programId: Long): Flow<List<Exercise>> =
        exerciseDao.getExercisesByProgramId(programId)
    
    suspend fun insertExercise(exercise: Exercise): Long = exerciseDao.insertExercise(exercise)
    
    suspend fun updateExercise(exercise: Exercise) = exerciseDao.updateExercise(exercise)
    
    suspend fun deleteExercise(exercise: Exercise) = exerciseDao.deleteExercise(exercise)

    // Goal
    fun getAllGoals(): Flow<List<Goal>> = goalDao.getAllGoals()
    
    suspend fun insertGoal(goal: Goal): Long = goalDao.insertGoal(goal)
    
    suspend fun updateGoal(goal: Goal) = goalDao.updateGoal(goal)
    
    suspend fun deleteGoal(goal: Goal) = goalDao.deleteGoal(goal)
}
