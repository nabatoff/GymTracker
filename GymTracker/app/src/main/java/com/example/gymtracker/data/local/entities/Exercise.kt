package com.example.gymtracker.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercises",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutProgram::class,
            parentColumns = ["programId"],
            childColumns = ["programId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index("programId")]
)
data class Exercise(
    @PrimaryKey(autoGenerate = true) val exerciseId: Long = 0,
    val programId: Long,
    val name: String,
    val sets: Int,
    val reps: String
)
