package com.example.gymtracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.gymtracker.data.local.entity.Exercise
import com.example.gymtracker.data.local.entity.ProgramWithExercises
import com.example.gymtracker.data.local.entity.WorkoutProgram
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutProgramDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgram(program: WorkoutProgram): Long

    @Update
    suspend fun updateProgram(program: WorkoutProgram)

    @Delete
    suspend fun deleteProgram(program: WorkoutProgram)

    @Query("SELECT * FROM workout_programs ORDER BY name ASC")
    fun getAllPrograms(): Flow<List<WorkoutProgram>>

    @Transaction
    @Query("SELECT * FROM workout_programs ORDER BY name ASC")
    fun getAllProgramsWithExercises(): Flow<List<ProgramWithExercises>>

    @Transaction
    @Query("SELECT * FROM workout_programs WHERE programId = :programId")
    fun getProgramWithExercises(programId: Long): Flow<ProgramWithExercises?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: Exercise): Long

    @Update
    suspend fun updateExercise(exercise: Exercise)

    @Delete
    suspend fun deleteExercise(exercise: Exercise)

    @Query("SELECT * FROM exercises WHERE programId = :programId")
    fun getExercisesForProgram(programId: Long): Flow<List<Exercise>>
}
