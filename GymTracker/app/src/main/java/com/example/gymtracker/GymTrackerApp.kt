package com.example.gymtracker

import android.app.Application
import com.example.gymtracker.data.AppContainer
import com.example.gymtracker.data.DefaultAppContainer
import kotlinx.coroutines.launch

class GymTrackerApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
        container.applicationScope.launch {
            container.repository.ensureDefaultGoals()
        }
    }
}
