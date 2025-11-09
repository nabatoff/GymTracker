package com.example.gymtracker.data.dao

import androidx.room.*
import com.example.gymtracker.data.entities.WorkoutSession
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutSessionDao {
    @Query("SELECT * FROM workout_sessions ORDER BY startTime DESC")
    fun getAllSessionsSortedByDate(): Flow<List<WorkoutSession>>

    @Query("SELECT * FROM workout_sessions WHERE id = :id")
    suspend fun getSessionById(id: Long): WorkoutSession?

    @Insert
    suspend fun insertSession(session: WorkoutSession): Long

    @Update
    suspend fun updateSession(session: WorkoutSession)

    @Delete
    suspend fun deleteSession(session: WorkoutSession)

    @Query("SELECT COUNT(*) FROM workout_sessions WHERE startTime >= :startOfMonth AND startTime < :endOfMonth")
    suspend fun getSessionsCountInMonth(startOfMonth: Long, endOfMonth: Long): Int

    @Query("SELECT COALESCE(SUM(durationInMinutes), 0) FROM workout_sessions WHERE startTime >= :startOfMonth AND startTime < :endOfMonth AND durationInMinutes IS NOT NULL")
    suspend fun getTotalDurationInMonth(startOfMonth: Long, endOfMonth: Long): Int
}
