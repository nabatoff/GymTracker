package com.example.gymtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.gymtracker.data.database.GymTrackerDatabase
import com.example.gymtracker.ui.navigation.GymTrackerNavigation
import com.example.gymtracker.ui.theme.GymTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val database = GymTrackerDatabase.getDatabase(applicationContext)
        val repository = com.example.gymtracker.data.repository.AppRepository(
            workoutSessionDao = database.workoutSessionDao(),
            bodyMetricDao = database.bodyMetricDao(),
            workoutProgramDao = database.workoutProgramDao(),
            exerciseDao = database.exerciseDao(),
            goalDao = database.goalDao()
        )

        setContent {
            GymTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    GymTrackerNavigation(repository = repository)
                }
            }
        }
    }
}
