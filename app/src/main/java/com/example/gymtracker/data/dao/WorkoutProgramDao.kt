package com.example.gymtracker.data.dao

import androidx.room.*
import com.example.gymtracker.data.entities.WorkoutProgram
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutProgramDao {
    @Query("SELECT * FROM workout_programs ORDER BY name ASC")
    fun getAllPrograms(): Flow<List<WorkoutProgram>>

    @Query("SELECT * FROM workout_programs WHERE programId = :programId")
    suspend fun getProgramById(programId: Long): WorkoutProgram?

    @Insert
    suspend fun insertProgram(program: WorkoutProgram): Long

    @Update
    suspend fun updateProgram(program: WorkoutProgram)

    @Delete
    suspend fun deleteProgram(program: WorkoutProgram)
}
