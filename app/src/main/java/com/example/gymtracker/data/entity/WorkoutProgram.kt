package com.example.gymtracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_programs")
data class WorkoutProgram(
    @PrimaryKey(autoGenerate = true)
    val programId: Long = 0,
    val name: String
)
