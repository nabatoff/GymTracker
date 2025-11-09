package com.example.gymtracker

import android.app.Application
import androidx.room.Room
import com.example.gymtracker.data.local.AppDatabase
import com.example.gymtracker.data.local.entity.Goal
import com.example.gymtracker.data.repository.AppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GymTrackerApplication : Application() {

    lateinit var appRepository: AppRepository
        private set

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "gym_tracker_db"
        ).fallbackToDestructiveMigration()
            .build()

        appRepository = AppRepository(
            workoutSessionDao = database.workoutSessionDao(),
            bodyMetricDao = database.bodyMetricDao(),
            workoutProgramDao = database.workoutProgramDao(),
            goalDao = database.goalDao()
        )

        seedGoalsIfNeeded()
    }

    private fun seedGoalsIfNeeded() {
        val defaultGoals = listOf(
            Goal(title = "Тренировок в месяц", targetValue = 12f, currentValue = 0f, unit = "тренировок"),
            Goal(title = "Целевой вес", targetValue = 80f, currentValue = 0f, unit = "кг"),
            Goal(title = "Время в зале (в мес.)", targetValue = 1000f, currentValue = 0f, unit = "минут")
        )
        applicationScope.launch {
            appRepository.ensureDefaultGoals(defaultGoals)
        }
    }
}
