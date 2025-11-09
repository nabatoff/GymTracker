package com.example.gymtracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gymtracker.data.local.entity.Goal
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: Goal): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(goals: List<Goal>)

    @Update
    suspend fun update(goal: Goal)

    @Delete
    suspend fun delete(goal: Goal)

    @Query("SELECT * FROM goals ORDER BY goalId ASC")
    fun getGoals(): Flow<List<Goal>>

    @Query("SELECT * FROM goals WHERE goalId = :id LIMIT 1")
    suspend fun getGoalById(id: Long): Goal?

    @Query("SELECT COUNT(*) FROM goals")
    suspend fun count(): Int
}
