package com.example.gymtracker.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.gymtracker.R

sealed class GymTrackerDestination(
    val route: String,
    @StringRes val label: Int,
    val icon: ImageVector
) {
    data object Home : GymTrackerDestination("home", R.string.home_title, Icons.Default.FitnessCenter)
    data object Programs : GymTrackerDestination("programs", R.string.programs_title, Icons.Default.CalendarMonth)
    data object Progress : GymTrackerDestination("progress", R.string.progress_title, Icons.Default.BarChart)
    data object History : GymTrackerDestination("history", R.string.history_title, Icons.Default.History)

    companion object {
        val bottomDestinations = listOf(Home, Programs, Progress, History)
    }
}

object ProgramRoutes {
    const val Detail = "programDetail"
    const val DetailWithArg = "$Detail/{programId}"
    const val ProgramIdArg = "programId"
}
