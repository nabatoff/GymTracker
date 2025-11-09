package com.example.gymtracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.gymtracker.ui.screens.history.HistoryScreen
import com.example.gymtracker.ui.screens.home.HomeScreen
import com.example.gymtracker.ui.screens.programs.ProgramDetailScreen
import com.example.gymtracker.ui.screens.programs.ProgramsScreen
import com.example.gymtracker.ui.screens.progress.ProgressScreen
import com.example.gymtracker.ui.viewmodel.GymTrackerViewModelFactory
import com.example.gymtracker.ui.viewmodel.HistoryViewModel
import com.example.gymtracker.ui.viewmodel.HomeViewModel
import com.example.gymtracker.ui.viewmodel.ProgramsViewModel
import com.example.gymtracker.ui.viewmodel.ProgressViewModel

@Composable
fun GymTrackerNavHost(
    navController: NavHostController,
    viewModelFactory: GymTrackerViewModelFactory,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = GymTrackerDestination.Home.route,
        modifier = modifier
    ) {
        addHomeDestination(viewModelFactory)
        addProgramsDestination(navController, viewModelFactory)
        addProgressDestination(viewModelFactory)
        addHistoryDestination(viewModelFactory)
    }
}

private fun NavGraphBuilder.addHomeDestination(factory: GymTrackerViewModelFactory) {
    composable(GymTrackerDestination.Home.route) { backStackEntry ->
        val viewModel: HomeViewModel = viewModel(backStackEntry, factory = factory)
        HomeScreen(viewModel = viewModel)
    }
}

private fun NavGraphBuilder.addProgramsDestination(
    navController: NavHostController,
    factory: GymTrackerViewModelFactory
) {
    composable(GymTrackerDestination.Programs.route) { backStackEntry ->
        val viewModel: ProgramsViewModel = viewModel(backStackEntry, factory = factory)
        ProgramsScreen(
            viewModel = viewModel,
            onProgramSelected = { programId ->
                navController.navigate("${ProgramRoutes.Detail}/$programId")
            }
        )
    }

    composable(
        route = ProgramRoutes.DetailWithArg,
        arguments = listOf(navArgument(ProgramRoutes.ProgramIdArg) { type = NavType.LongType })
    ) { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(GymTrackerDestination.Programs.route)
        }
        val programsViewModel: ProgramsViewModel = viewModel(parentEntry, factory = factory)
        val programId = backStackEntry.arguments?.getLong(ProgramRoutes.ProgramIdArg) ?: return@composable
        ProgramDetailScreen(
            programId = programId,
            viewModel = programsViewModel,
            onBack = { navController.popBackStack() }
        )
    }
}

private fun NavGraphBuilder.addProgressDestination(factory: GymTrackerViewModelFactory) {
    composable(GymTrackerDestination.Progress.route) { backStackEntry ->
        val viewModel: ProgressViewModel = viewModel(backStackEntry, factory = factory)
        ProgressScreen(viewModel = viewModel)
    }
}

private fun NavGraphBuilder.addHistoryDestination(factory: GymTrackerViewModelFactory) {
    composable(GymTrackerDestination.History.route) { backStackEntry ->
        val viewModel: HistoryViewModel = viewModel(backStackEntry, factory = factory)
        HistoryScreen(viewModel = viewModel)
    }
}
