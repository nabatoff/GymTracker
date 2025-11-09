package com.example.gymtracker.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gymtracker.R
import com.example.gymtracker.ui.history.HistoryScreen
import com.example.gymtracker.ui.history.HistoryViewModel
import com.example.gymtracker.ui.home.HomeScreen
import com.example.gymtracker.ui.home.HomeViewModel
import com.example.gymtracker.ui.progress.ProgressScreen
import com.example.gymtracker.ui.progress.ProgressViewModel
import com.example.gymtracker.ui.programs.ProgramDetailScreen
import com.example.gymtracker.ui.programs.ProgramsScreen
import com.example.gymtracker.ui.programs.ProgramsViewModel

private enum class MainDestination(
    val route: String,
    @StringRes val labelRes: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Home("home", R.string.nav_home, Icons.Default.Home),
    Programs("programs", R.string.nav_programs, Icons.Default.ViewList),
    Progress("progress", R.string.nav_progress, Icons.Default.BarChart),
    History("history", R.string.nav_history, Icons.Default.History);
}

const val PROGRAM_DETAIL_ROUTE = "programDetail"

@Composable
fun GymTrackerAppRoot(
    viewModelFactory: ViewModelProvider.Factory
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            if (currentDestination.shouldShowBottomBar()) {
                NavigationBar {
                    MainDestination.entries.forEach { destination ->
                        NavigationBarItem(
                            selected = currentDestination.isBottomDestination(destination),
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(destination.icon, contentDescription = null) },
                            label = { Text(text = stringResource(id = destination.labelRes)) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MainDestination.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(MainDestination.Home.route) {
                val viewModel: HomeViewModel = viewModel(factory = viewModelFactory)
                HomeScreen(
                    stateFlow = viewModel.uiState,
                    onStart = viewModel::startWorkout,
                    onStop = viewModel::stopWorkout
                )
            }

            composable(MainDestination.Programs.route) {
                val viewModel: ProgramsViewModel = viewModel(factory = viewModelFactory)
                ProgramsScreen(
                    stateFlow = viewModel.uiState,
                    onCreateProgram = viewModel::addProgram,
                    onUpdateProgram = viewModel::updateProgram,
                    onDeleteProgram = viewModel::deleteProgram,
                    onProgramClick = { programId ->
                        navController.navigate("$PROGRAM_DETAIL_ROUTE/$programId")
                    }
                )
            }

            composable(
                route = "$PROGRAM_DETAIL_ROUTE/{programId}",
                arguments = listOf(navArgument("programId") { type = NavType.LongType })
            ) { backStackEntry ->
                val programId = backStackEntry.arguments?.getLong("programId") ?: return@composable
                val viewModel: ProgramsViewModel = viewModel(factory = viewModelFactory)
                ProgramDetailScreen(
                    programId = programId,
                    programFlow = viewModel.observeProgram(programId),
                    onBack = { navController.popBackStack() },
                    onAddExercise = viewModel::addExercise,
                    onUpdateExercise = viewModel::updateExercise,
                    onDeleteExercise = viewModel::deleteExercise,
                    onRenameProgram = viewModel::updateProgram,
                    onDeleteProgram = {
                        viewModel.deleteProgram(it)
                        navController.popBackStack()
                    }
                )
            }

            composable(MainDestination.Progress.route) {
                val viewModel: ProgressViewModel = viewModel(factory = viewModelFactory)
                ProgressScreen(
                    metricsState = viewModel.metrics,
                    goalProgressState = viewModel.goalProgress,
                    isSavingState = viewModel.isSaving,
                    onSaveMetric = viewModel::saveMetric
                )
            }

            composable(MainDestination.History.route) {
                val viewModel: HistoryViewModel = viewModel(factory = viewModelFactory)
                HistoryScreen(
                    sessionsState = viewModel.sessions
                )
            }
        }
    }
}

private fun NavDestination?.isBottomDestination(destination: MainDestination): Boolean {
    return this?.hierarchy?.any { it.route == destination.route } == true
}

private fun NavDestination?.shouldShowBottomBar(): Boolean {
    return MainDestination.entries.any { isBottomDestination(it) }
}
