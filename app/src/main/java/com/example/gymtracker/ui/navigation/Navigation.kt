package com.example.gymtracker.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavArgument
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gymtracker.ui.history.HistoryScreen
import com.example.gymtracker.ui.home.HomeScreen
import com.example.gymtracker.ui.programs.ProgramDetailScreen
import com.example.gymtracker.ui.programs.ProgramsScreen
import com.example.gymtracker.ui.progress.ProgressScreen

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : Screen("home", "Главная", Icons.Default.Home)
    object Programs : Screen("programs", "Программы", Icons.Default.FitnessCenter)
    object Progress : Screen("progress", "Прогресс", Icons.Default.TrendingUp)
    object History : Screen("history", "История", Icons.Default.History)
    object ProgramDetail : Screen("program_detail/{programId}", "Детали программы", Icons.Default.FitnessCenter) {
        fun createRoute(programId: Long) = "program_detail/$programId"
    }
}

@Composable
fun GymTrackerNavigation(
    repository: com.example.gymtracker.data.repository.AppRepository,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    androidx.compose.material3.Scaffold(
        bottomBar = {
            NavigationBar {
                listOf(
                    Screen.Home,
                    Screen.Programs,
                    Screen.Progress,
                    Screen.History
                ).forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = modifier.padding(padding)
        ) {
            composable(Screen.Home.route) {
                com.example.gymtracker.ui.home.HomeScreen(
                    viewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                            @Suppress("UNCHECKED_CAST")
                            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                                return com.example.gymtracker.ui.home.HomeViewModel(repository) as T
                            }
                        }
                    )
                )
            }
            composable(Screen.Programs.route) {
                ProgramsScreen(
                    onProgramClick = { programId ->
                        navController.navigate(Screen.ProgramDetail.createRoute(programId))
                    },
                    viewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                            @Suppress("UNCHECKED_CAST")
                            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                                return com.example.gymtracker.ui.programs.ProgramsViewModel(repository) as T
                            }
                        }
                    )
                )
            }
            composable(Screen.Progress.route) {
                ProgressScreen(repository = repository)
            }
            composable(Screen.History.route) {
                HistoryScreen(repository = repository)
            }
            composable(
                route = Screen.ProgramDetail.route,
                arguments = listOf(
                    NavArgument("programId") {
                        type = NavType.LongType
                    }
                )
            ) { backStackEntry ->
                val programId = backStackEntry.arguments?.getLong("programId") ?: return@composable
                ProgramDetailScreen(
                    programId = programId,
                    repository = repository,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
