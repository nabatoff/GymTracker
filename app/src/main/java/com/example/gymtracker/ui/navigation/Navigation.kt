package com.example.gymtracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gymtracker.ui.history.HistoryScreen
import com.example.gymtracker.ui.home.HomeScreen
import com.example.gymtracker.ui.programs.ProgramDetailScreen
import com.example.gymtracker.ui.programs.ProgramsScreen
import com.example.gymtracker.ui.progress.ProgressScreen
import com.example.gymtracker.ui.viewmodel.ViewModelFactory

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Programs : Screen("programs")
    object Progress : Screen("progress")
    object History : Screen("history")
    object ProgramDetail : Screen("program_detail/{programId}") {
        fun createRoute(programId: Long) = "program_detail/$programId"
    }
}

@Composable
fun GymTrackerNavigation(
    navController: NavHostController = rememberNavController(),
    viewModelFactory: ViewModelFactory? = null
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = if (viewModelFactory != null) {
                    viewModel(factory = viewModelFactory)
                } else {
                    viewModel()
                }
            )
        }
        composable(Screen.Programs.route) {
            ProgramsScreen(
                navController = navController,
                viewModel = if (viewModelFactory != null) {
                    viewModel(factory = viewModelFactory)
                } else {
                    viewModel()
                }
            )
        }
        composable(Screen.Progress.route) {
            ProgressScreen(
                viewModel = if (viewModelFactory != null) {
                    viewModel(factory = viewModelFactory)
                } else {
                    viewModel()
                }
            )
        }
        composable(Screen.History.route) {
            HistoryScreen(
                viewModel = if (viewModelFactory != null) {
                    viewModel(factory = viewModelFactory)
                } else {
                    viewModel()
                }
            )
        }
        composable(Screen.ProgramDetail.route) { backStackEntry ->
            val programId = backStackEntry.arguments?.getString("programId")?.toLongOrNull() ?: 0L
            ProgramDetailScreen(
                programId = programId,
                navController = navController,
                viewModel = if (viewModelFactory != null) {
                    viewModel(factory = viewModelFactory)
                } else {
                    viewModel()
                }
            )
        }
    }
}
