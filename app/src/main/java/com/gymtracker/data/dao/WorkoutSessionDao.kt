package com.gymtracker.data.dao

import androidx.room.*
import com.gymtracker.data.entities.WorkoutSession
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutSessionDao {
    @Query("SELECT * FROM workout_sessions ORDER BY startTime DESC")
    fun getAllSessionsSortedByDate(): Flow<List<WorkoutSession>>

    @Query("SELECT * FROM workout_sessions WHERE id = :id")
    suspend fun getSessionById(id: Long): WorkoutSession?

    @Insert
    suspend fun insert(session: WorkoutSession): Long

    @Update
    suspend fun update(session: WorkoutSession)

    @Delete
    suspend fun delete(session: WorkoutSession)

    @Query("SELECT * FROM workout_sessions WHERE startTime >= :startTime AND startTime <= :endTime")
    suspend fun getSessionsInRange(startTime: Long, endTime: Long): List<WorkoutSession>

    @Query("SELECT SUM(durationInMinutes) FROM workout_sessions WHERE startTime >= :startTime AND startTime <= :endTime")
    suspend fun getTotalDurationInRange(startTime: Long, endTime: Long): Int?

    @Query("SELECT COUNT(*) FROM workout_sessions WHERE startTime >= :startTime AND startTime <= :endTime")
    suspend fun getSessionCountInRange(startTime: Long, endTime: Long): Int
}
