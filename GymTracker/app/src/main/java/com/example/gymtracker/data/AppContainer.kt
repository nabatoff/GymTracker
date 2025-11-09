package com.example.gymtracker.data

import android.content.Context
import androidx.room.Room
import com.example.gymtracker.data.local.AppDatabase
import com.example.gymtracker.data.repository.AppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

interface AppContainer {
    val repository: AppRepository
    val applicationScope: CoroutineScope
}

class DefaultAppContainer(context: Context) : AppContainer {
    private val database: AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "gym_tracker_db"
    )
        .fallbackToDestructiveMigration()
        .build()

    override val applicationScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override val repository: AppRepository by lazy {
        AppRepository(
            workoutSessionDao = database.workoutSessionDao(),
            bodyMetricDao = database.bodyMetricDao(),
            workoutProgramDao = database.workoutProgramDao(),
            goalDao = database.goalDao()
        )
    }
}
