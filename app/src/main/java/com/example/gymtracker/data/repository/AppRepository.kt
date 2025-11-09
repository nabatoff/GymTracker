package com.example.gymtracker.data.repository

import com.example.gymtracker.data.dao.*
import com.example.gymtracker.data.entity.*
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

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
    
    suspend fun getSessionsCountForMonth(startOfMonth: Long, endOfMonth: Long): Int =
        workoutSessionDao.getSessionsCountForMonth(startOfMonth, endOfMonth)
    
    suspend fun getTotalDurationForMonth(startOfMonth: Long, endOfMonth: Long): Int? =
        workoutSessionDao.getTotalDurationForMonth(startOfMonth, endOfMonth)

    // BodyMetric
    fun getAllMetrics(): Flow<List<BodyMetric>> = bodyMetricDao.getAllMetricsSortedByDate()
    
    suspend fun getMetricById(id: Long): BodyMetric? = bodyMetricDao.getMetricById(id)
    
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
    
    suspend fun getExerciseById(exerciseId: Long): Exercise? = exerciseDao.getExerciseById(exerciseId)
    
    suspend fun insertExercise(exercise: Exercise): Long = exerciseDao.insertExercise(exercise)
    
    suspend fun updateExercise(exercise: Exercise) = exerciseDao.updateExercise(exercise)
    
    suspend fun deleteExercise(exercise: Exercise) = exerciseDao.deleteExercise(exercise)

    // Goal
    fun getAllGoals(): Flow<List<Goal>> = goalDao.getAllGoals()
    
    suspend fun getGoalById(goalId: Long): Goal? = goalDao.getGoalById(goalId)
    
    suspend fun insertGoal(goal: Goal): Long = goalDao.insertGoal(goal)
    
    suspend fun updateGoal(goal: Goal) = goalDao.updateGoal(goal)
    
    suspend fun deleteGoal(goal: Goal) = goalDao.deleteGoal(goal)

    // Helper methods
    suspend fun getCurrentMonthStart(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    suspend fun getCurrentMonthEnd(): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, 1)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
