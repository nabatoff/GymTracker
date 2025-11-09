package com.example.gymtracker.ui.foldable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.gymtracker.ui.BottomNavigationBar
import com.example.gymtracker.ui.navigation.GymTrackerNavigation
import com.example.gymtracker.ui.navigation.Screen
import com.example.gymtracker.ui.viewmodel.ViewModelFactory

@Composable
fun AdaptiveLayout(
    foldableState: FoldableState,
    navController: NavController,
    viewModelFactory: ViewModelFactory
) {
    when {
        // Режим "книга" (вертикальная складка, разложено) - два экрана рядом
        // Оптимизировано для Samsung Galaxy Fold 6 в портретной ориентации
        foldableState.isBookMode -> {
            Row(modifier = Modifier.fillMaxSize()) {
                // Левая панель - навигация
                Surface(
                    modifier = Modifier
                        .width(300.dp)
                        .fillMaxHeight(),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column {
                        Text(
                            text = "GymTracker",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                        Divider()
                        NavigationSidebar(navController = navController)
                    }
                }
                Divider(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight(),
                    color = MaterialTheme.colorScheme.outline
                )
                // Правая панель - контент
                Box(modifier = Modifier.weight(1f)) {
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
        // Режим "столик" (горизонтальная складка, разложено) - два экрана друг над другом
        // Оптимизировано для Samsung Galaxy Fold 6 в альбомной ориентации
        foldableState.isTabletopMode -> {
            Column(modifier = Modifier.fillMaxSize()) {
                // Верхняя панель - навигация
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column {
                        Text(
                            text = "GymTracker",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                        Divider()
                        NavigationSidebarHorizontal(navController = navController)
                    }
                }
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp),
                    color = MaterialTheme.colorScheme.outline
                )
                // Нижняя панель - контент
                Box(modifier = Modifier.weight(1f)) {
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
        // Полностью разложенный режим - используем весь экран с оптимизацией для большого экрана
        foldableState.isFlat && !foldableState.isFolded -> {
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
        // Обычный режим (сложено или обычный телефон)
        else -> {
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

@Composable
fun NavigationSidebar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val navItems = listOf(
        "Главная" to Screen.Home.route,
        "Программы" to Screen.Programs.route,
        "Прогресс" to Screen.Progress.route,
        "История" to Screen.History.route
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        navItems.forEach { (title, route) ->
            NavigationDrawerItem(
                label = { Text(title) },
                selected = currentRoute == route,
                onClick = {
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun NavigationSidebarHorizontal(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val navItems = listOf(
        "Главная" to Screen.Home.route,
        "Программы" to Screen.Programs.route,
        "Прогресс" to Screen.Progress.route,
        "История" to Screen.History.route
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        navItems.forEach { (title, route) ->
            FilterChip(
                selected = currentRoute == route,
                onClick = {
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                label = { Text(title) }
            )
        }
    }
}
