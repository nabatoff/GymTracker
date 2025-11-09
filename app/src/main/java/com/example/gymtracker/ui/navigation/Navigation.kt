package com.example.gymtracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import com.example.gymtracker.ui.calendar.CalendarScreen
import com.example.gymtracker.ui.history.HistoryScreen
import com.example.gymtracker.ui.home.HomeScreen
import com.example.gymtracker.ui.programs.ProgramDetailScreen
import com.example.gymtracker.ui.programs.ProgramsScreen
import com.example.gymtracker.ui.progress.AdvancedStatsScreen
import com.example.gymtracker.ui.progress.ProgressScreen
import com.example.gymtracker.ui.settings.SettingsScreen
import com.example.gymtracker.ui.share.ShareProgressScreen
import com.example.gymtracker.ui.viewmodel.ViewModelFactory

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Programs : Screen("programs")
    object Progress : Screen("progress")
    object History : Screen("history")
    object Settings : Screen("settings")
    object Calendar : Screen("calendar")
    object AdvancedStats : Screen("advanced_stats")
    object Share : Screen("share")
    object ProgramDetail : Screen("program_detail/{programId}") {
        fun createRoute(programId: Long) = "program_detail/$programId"
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GymTrackerNavigation(
    navController: NavHostController = rememberNavController(),
    viewModelFactory: ViewModelFactory? = null
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        enterTransition = {
            fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(300)
            )
        }
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
                navController = navController,
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
        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = if (viewModelFactory != null) {
                    viewModel(factory = viewModelFactory)
                } else {
                    viewModel()
                }
            )
        }
        composable(Screen.Calendar.route) {
            CalendarScreen(
                viewModel = if (viewModelFactory != null) {
                    viewModel(factory = viewModelFactory)
                } else {
                    viewModel()
                }
            )
        }
        composable(Screen.AdvancedStats.route) {
            AdvancedStatsScreen(
                viewModel = if (viewModelFactory != null) {
                    viewModel(factory = viewModelFactory)
                } else {
                    viewModel()
                }
            )
        }
        composable(Screen.Share.route) {
            ShareProgressScreen(
                viewModel = if (viewModelFactory != null) {
                    viewModel(factory = viewModelFactory)
                } else {
                    viewModel()
                }
            )
        }
    }
}
