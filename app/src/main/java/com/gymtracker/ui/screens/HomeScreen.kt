package com.gymtracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gymtracker.ui.viewmodels.HomeViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val isWorkoutActive by viewModel.isWorkoutActive.collectAsState()
    val elapsedSeconds by viewModel.elapsedSeconds.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "GymTracker",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (isWorkoutActive) {
                Text(
                    text = viewModel.formatElapsedTime(elapsedSeconds),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { viewModel.endWorkout() },
                    modifier = Modifier
                        .width(250.dp)
                        .height(80.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(
                        text = "Закончить тренировку",
                        fontSize = 18.sp
                    )
                }
            } else {
                Button(
                    onClick = { viewModel.startWorkout() },
                    modifier = Modifier
                        .width(250.dp)
                        .height(80.dp)
                ) {
                    Text(
                        text = "Начать тренировку",
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}
