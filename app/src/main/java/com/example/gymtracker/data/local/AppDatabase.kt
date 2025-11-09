package com.example.gymtracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.gymtracker.data.local.dao.BodyMetricDao
import com.example.gymtracker.data.local.dao.GoalDao
import com.example.gymtracker.data.local.dao.WorkoutProgramDao
import com.example.gymtracker.data.local.dao.WorkoutSessionDao
import com.example.gymtracker.data.local.entity.BodyMetric
import com.example.gymtracker.data.local.entity.Exercise
import com.example.gymtracker.data.local.entity.Goal
import com.example.gymtracker.data.local.entity.WorkoutProgram
import com.example.gymtracker.data.local.entity.WorkoutSession

@Database(
    entities = [
        WorkoutSession::class,
        BodyMetric::class,
        WorkoutProgram::class,
        Exercise::class,
        Goal::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workoutSessionDao(): WorkoutSessionDao
    abstract fun bodyMetricDao(): BodyMetricDao
    abstract fun workoutProgramDao(): WorkoutProgramDao
    abstract fun goalDao(): GoalDao
}
