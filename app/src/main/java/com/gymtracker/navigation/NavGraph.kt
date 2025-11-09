package com.gymtracker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.gymtracker.ui.screens.*
import com.gymtracker.ui.viewmodels.*

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
fun NavGraph(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    programsViewModel: ProgramsViewModel,
    progressViewModel: ProgressViewModel,
    historyViewModel: HistoryViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(viewModel = homeViewModel)
        }

        composable(Screen.Programs.route) {
            ProgramsScreen(
                viewModel = programsViewModel,
                onProgramClick = { programId ->
                    navController.navigate(Screen.ProgramDetail.createRoute(programId))
                }
            )
        }

        composable(Screen.Progress.route) {
            ProgressScreen(viewModel = progressViewModel)
        }

        composable(Screen.History.route) {
            HistoryScreen(viewModel = historyViewModel)
        }

        composable(
            route = Screen.ProgramDetail.route,
            arguments = listOf(
                navArgument("programId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val programId = backStackEntry.arguments?.getLong("programId") ?: return@composable
            ProgramDetailScreen(
                programId = programId,
                viewModel = programsViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
