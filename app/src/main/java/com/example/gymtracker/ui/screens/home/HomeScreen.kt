package com.example.gymtracker.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymtracker.R
import com.example.gymtracker.ui.viewmodel.HomeViewModel
import com.example.gymtracker.util.formatElapsedTime

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.isSessionActive) {
                Text(
                    text = formatElapsedTime(uiState.elapsedMillis),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 48.sp
                    )
                )
            }

            Button(
                onClick = {
                    if (uiState.isSessionActive) {
                        viewModel.endWorkout()
                    } else {
                        viewModel.startWorkout()
                    }
                },
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp),
                modifier = Modifier.padding(top = 32.dp)
            ) {
                Text(
                    text = if (uiState.isSessionActive) {
                        stringResource(R.string.end_workout)
                    } else {
                        stringResource(R.string.start_workout)
                    },
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}
