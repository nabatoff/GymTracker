package com.gymtracker

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gymtracker.data.database.AppDatabase
import com.gymtracker.data.repository.AppRepository
import com.gymtracker.navigation.NavGraph
import com.gymtracker.navigation.Screen
import com.gymtracker.ui.viewmodels.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GymTrackerApp() {
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val repository = AppRepository(
        workoutSessionDao = database.workoutSessionDao(),
        bodyMetricDao = database.bodyMetricDao(),
        workoutProgramDao = database.workoutProgramDao(),
        exerciseDao = database.exerciseDao(),
        goalDao = database.goalDao()
    )

    val homeViewModel = HomeViewModel(repository)
    val programsViewModel = ProgramsViewModel(repository)
    val progressViewModel = ProgressViewModel(repository)
    val historyViewModel = HistoryViewModel(repository)

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Определяем, нужно ли показывать нижнюю навигацию
    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Programs.route,
        Screen.Progress.route,
        Screen.History.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("Главная") },
                        selected = currentRoute == Screen.Home.route,
                        onClick = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.List, contentDescription = null) },
                        label = { Text("Программы") },
                        selected = currentRoute == Screen.Programs.route,
                        onClick = {
                            navController.navigate(Screen.Programs.route) {
                                popUpTo(Screen.Home.route)
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.ShowChart, contentDescription = null) },
                        label = { Text("Прогресс") },
                        selected = currentRoute == Screen.Progress.route,
                        onClick = {
                            navController.navigate(Screen.Progress.route) {
                                popUpTo(Screen.Home.route)
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.History, contentDescription = null) },
                        label = { Text("История") },
                        selected = currentRoute == Screen.History.route,
                        onClick = {
                            navController.navigate(Screen.History.route) {
                                popUpTo(Screen.Home.route)
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding)
        ) {
            NavGraph(
                navController = navController,
                homeViewModel = homeViewModel,
                programsViewModel = programsViewModel,
                progressViewModel = progressViewModel,
                historyViewModel = historyViewModel
            )
        }
    }
}
