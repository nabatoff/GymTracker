package com.example.gymtracker.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.gymtracker.data.dao.*
import com.example.gymtracker.data.entity.*

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
abstract class GymTrackerDatabase : RoomDatabase() {
    abstract fun workoutSessionDao(): WorkoutSessionDao
    abstract fun bodyMetricDao(): BodyMetricDao
    abstract fun workoutProgramDao(): WorkoutProgramDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun goalDao(): GoalDao

    companion object {
        @Volatile
        private var INSTANCE: GymTrackerDatabase? = null

        fun getDatabase(context: Context): GymTrackerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GymTrackerDatabase::class.java,
                    "gymtracker.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
