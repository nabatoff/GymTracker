package com.example.gymtracker.data.dao

import androidx.room.*
import com.example.gymtracker.data.entity.Exercise
import com.example.gymtracker.data.entity.WorkoutProgram
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutProgramDao {
    @Query("SELECT * FROM workout_programs ORDER BY programId DESC")
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

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises WHERE programId = :programId ORDER BY exerciseId")
    fun getExercisesByProgramId(programId: Long): Flow<List<Exercise>>

    @Query("SELECT * FROM exercises WHERE exerciseId = :exerciseId")
    suspend fun getExerciseById(exerciseId: Long): Exercise?

    @Insert
    suspend fun insertExercise(exercise: Exercise): Long

    @Update
    suspend fun updateExercise(exercise: Exercise)

    @Delete
    suspend fun deleteExercise(exercise: Exercise)
}
