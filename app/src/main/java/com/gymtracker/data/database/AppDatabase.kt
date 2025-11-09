package com.gymtracker.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gymtracker.data.dao.*
import com.gymtracker.data.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
    abstract fun exerciseDao(): ExerciseDao
    abstract fun goalDao(): GoalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gym_tracker_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database)
                }
            }
        }

        suspend fun populateDatabase(database: AppDatabase) {
            val goalDao = database.goalDao()

            // Предзаполняем базовые цели
            goalDao.insert(
                Goal(
                    title = "Тренировок в месяц",
                    targetValue = 12f,
                    currentValue = 0f,
                    unit = "тренировок"
                )
            )
            goalDao.insert(
                Goal(
                    title = "Целевой вес",
                    targetValue = 80f,
                    currentValue = 0f,
                    unit = "кг"
                )
            )
            goalDao.insert(
                Goal(
                    title = "Время в зале (в мес.)",
                    targetValue = 1000f,
                    currentValue = 0f,
                    unit = "минут"
                )
            )
        }
    }
}
