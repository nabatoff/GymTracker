package com.example.gymtracker

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHostController
import com.example.gymtracker.ui.navigation.GymTrackerDestination
import com.example.gymtracker.ui.navigation.GymTrackerNavHost
import com.example.gymtracker.ui.theme.GymTrackerTheme
import com.example.gymtracker.ui.viewmodel.GymTrackerViewModelFactory

@Composable
fun GymTrackerApp(
    viewModelFactory: GymTrackerViewModelFactory
) {
    GymTrackerTheme {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val bottomDestinations = GymTrackerDestination.bottomDestinations

        Scaffold(
            bottomBar = {
                NavigationBar {
                    bottomDestinations.forEach { destination ->
                        val selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navigateToRootDestination(navController, destination.route)
                            },
                            icon = { Icon(destination.icon, contentDescription = null) },
                            label = { Text(text = stringResource(id = destination.label)) },
                            alwaysShowLabel = false
                        )
                    }
                }
            }
        ) { paddingValues ->
            GymTrackerNavHost(
                navController = navController,
                viewModelFactory = viewModelFactory,
                modifier = Modifier
                    .padding(paddingValues)
            )
        }
    }
}

private fun navigateToRootDestination(navController: NavHostController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
