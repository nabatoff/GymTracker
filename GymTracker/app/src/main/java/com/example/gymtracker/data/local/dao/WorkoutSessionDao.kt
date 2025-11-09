package com.example.gymtracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gymtracker.data.local.entities.WorkoutSession
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: WorkoutSession): Long

    @Update
    suspend fun update(session: WorkoutSession)

    @Delete
    suspend fun delete(session: WorkoutSession)

    @Query("SELECT * FROM workout_sessions WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): WorkoutSession?

    @Query("SELECT * FROM workout_sessions ORDER BY startTime DESC")
    fun getAllSessionsSortedByDate(): Flow<List<WorkoutSession>>

    @Query("SELECT COUNT(*) FROM workout_sessions WHERE startTime BETWEEN :startTime AND :endTime")
    suspend fun countSessionsBetween(startTime: Long, endTime: Long): Int

    @Query("SELECT COALESCE(SUM(durationInMinutes), 0) FROM workout_sessions WHERE startTime BETWEEN :startTime AND :endTime")
    suspend fun sumDurationBetween(startTime: Long, endTime: Long): Int
}
