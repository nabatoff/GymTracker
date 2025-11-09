package com.gymtracker.data.dao

import androidx.room.*
import com.gymtracker.data.entities.WorkoutProgram
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutProgramDao {
    @Query("SELECT * FROM workout_programs ORDER BY name ASC")
    fun getAllPrograms(): Flow<List<WorkoutProgram>>

    @Query("SELECT * FROM workout_programs WHERE programId = :id")
    suspend fun getProgramById(id: Long): WorkoutProgram?

    @Insert
    suspend fun insert(program: WorkoutProgram): Long

    @Update
    suspend fun update(program: WorkoutProgram)

    @Delete
    suspend fun delete(program: WorkoutProgram)
}
