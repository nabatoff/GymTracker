package com.gymtracker.data.dao

import androidx.room.*
import com.gymtracker.data.entities.Exercise
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises WHERE programId = :programId")
    fun getExercisesForProgram(programId: Long): Flow<List<Exercise>>

    @Query("SELECT * FROM exercises WHERE exerciseId = :id")
    suspend fun getExerciseById(id: Long): Exercise?

    @Insert
    suspend fun insert(exercise: Exercise)

    @Update
    suspend fun update(exercise: Exercise)

    @Delete
    suspend fun delete(exercise: Exercise)

    @Query("DELETE FROM exercises WHERE programId = :programId")
    suspend fun deleteAllForProgram(programId: Long)
}
