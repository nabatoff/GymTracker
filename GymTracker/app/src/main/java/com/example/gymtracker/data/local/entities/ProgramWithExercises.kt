package com.example.gymtracker.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation

data class ProgramWithExercises(
    @Embedded val program: WorkoutProgram,
    @Relation(
        parentColumn = "programId",
        entityColumn = "programId"
    )
    val exercises: List<Exercise>
)
