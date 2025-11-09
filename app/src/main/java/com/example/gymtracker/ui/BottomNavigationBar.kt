package com.example.gymtracker.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.gymtracker.ui.navigation.Screen

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem(Screen.Home.route, "Главная", Icons.Default.Home)
    object Programs : BottomNavItem(Screen.Programs.route, "Программы", Icons.Default.FitnessCenter)
    object Progress : BottomNavItem(Screen.Progress.route, "Прогресс", Icons.Default.TrendingUp)
    object History : BottomNavItem(Screen.History.route, "История", Icons.Default.History)
    object Calendar : BottomNavItem(Screen.Calendar.route, "Календарь", Icons.Default.CalendarToday)
    object Settings : BottomNavItem(Screen.Settings.route, "Настройки", Icons.Default.Settings)
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Programs,
        BottomNavItem.Progress,
        BottomNavItem.History,
        BottomNavItem.Calendar,
        BottomNavItem.Settings
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
