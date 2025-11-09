package com.example.gymtracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.gymtracker.data.local.entities.Exercise
import com.example.gymtracker.data.local.entities.ProgramWithExercises
import com.example.gymtracker.data.local.entities.WorkoutProgram
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutProgramDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgram(program: WorkoutProgram): Long

    @Update
    suspend fun updateProgram(program: WorkoutProgram)

    @Delete
    suspend fun deleteProgram(program: WorkoutProgram)

    @Transaction
    @Query("SELECT * FROM workout_programs ORDER BY name ASC")
    fun getProgramsWithExercises(): Flow<List<ProgramWithExercises>>

    @Transaction
    @Query("SELECT * FROM workout_programs WHERE programId = :programId LIMIT 1")
    fun observeProgramWithExercises(programId: Long): Flow<ProgramWithExercises?>

    @Transaction
    @Query("SELECT * FROM workout_programs WHERE programId = :programId LIMIT 1")
    suspend fun getProgramWithExercises(programId: Long): ProgramWithExercises?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: Exercise): Long

    @Update
    suspend fun updateExercise(exercise: Exercise)

    @Delete
    suspend fun deleteExercise(exercise: Exercise)

    @Query("SELECT * FROM exercises WHERE exerciseId = :exerciseId LIMIT 1")
    suspend fun getExerciseById(exerciseId: Long): Exercise?
}
