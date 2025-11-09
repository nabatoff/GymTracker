package com.example.gymtracker.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.gymtracker.data.database.GymTrackerDatabase
import com.example.gymtracker.data.repository.AppRepository
import com.example.gymtracker.ui.navigation.GymTrackerNavigation
import com.example.gymtracker.ui.theme.GymTrackerTheme
import com.example.gymtracker.ui.viewmodel.ViewModelFactory

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

        val viewModelFactory = ViewModelFactory(repository)

        setContent {
            GymTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    Scaffold(
                        bottomBar = {
                            BottomNavigationBar(navController = navController)
                        }
                    ) { paddingValues ->
                        Box(modifier = Modifier.padding(paddingValues)) {
                            GymTrackerNavigation(
                                navController = navController,
                                viewModelFactory = viewModelFactory
                            )
                        }
                    }
                }
            }
        }
    }
}
