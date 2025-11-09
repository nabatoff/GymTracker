package com.example.gymtracker.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.gymtracker.data.database.GymTrackerDatabase
import com.example.gymtracker.data.repository.AppRepository
import com.example.gymtracker.ui.foldable.rememberFoldableState
import com.example.gymtracker.ui.navigation.GymTrackerNavigation
import com.example.gymtracker.ui.theme.GymTrackerTheme
import com.example.gymtracker.ui.viewmodel.ViewModelFactory
import com.example.gymtracker.util.PreferencesManager
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = GymTrackerDatabase.getDatabase(applicationContext)
        val repository = AppRepository(
            workoutSessionDao = database.workoutSessionDao(),
            bodyMetricDao = database.bodyMetricDao(),
            workoutProgramDao = database.workoutProgramDao(),
            exerciseDao = database.exerciseDao(),
            goalDao = database.goalDao()
        )
        
        val preferencesManager = PreferencesManager(applicationContext)
        val viewModelFactory = ViewModelFactory(repository, preferencesManager, applicationContext)

        setContent {
            val isDarkTheme by preferencesManager.isDarkTheme.collectAsState(initial = false)
            
            GymTrackerTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val foldableState by rememberFoldableState()
                    
                    AdaptiveLayout(
                        foldableState = foldableState,
                        navController = navController,
                        viewModelFactory = viewModelFactory
                    )
                }
            }
        }
    }
}
