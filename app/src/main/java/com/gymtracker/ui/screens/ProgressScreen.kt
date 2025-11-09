package com.gymtracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gymtracker.data.entities.Goal
import com.gymtracker.ui.viewmodels.ProgressViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(viewModel: ProgressViewModel) {
    val metrics by viewModel.metrics.collectAsState(initial = emptyList())
    val goals by viewModel.goals.collectAsState(initial = emptyList())
    val weightInput by viewModel.weightInput.collectAsState()
    val bodyFatInput by viewModel.bodyFatInput.collectAsState()
    val muscleMassInput by viewModel.muscleMassInput.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.updateGoalsWithCurrentData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Прогресс") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Форма ввода данных
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Ввод данных",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = weightInput,
                            onValueChange = { viewModel.updateWeightInput(it) },
                            label = { Text("Вес (кг)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = bodyFatInput,
                            onValueChange = { viewModel.updateBodyFatInput(it) },
                            label = { Text("% жира (опционально)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = muscleMassInput,
                            onValueChange = { viewModel.updateMuscleMassInput(it) },
                            label = { Text("Мышечная масса, кг (опционально)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.saveBodyMetric() },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = weightInput.toFloatOrNull() != null
                        ) {
                            Text("Сохранить")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // График веса (упрощенная версия - показываем последние значения)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "История веса",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        if (metrics.isEmpty()) {
                            Text("Нет данных для отображения")
                        } else {
                            metrics.take(5).forEach { metric ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale("ru"))
                                            .format(java.util.Date(metric.date))
                                    )
                                    Text(
                                        text = "${metric.weight} кг",
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Цели
            item {
                Text(
                    text = "Цели",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(goals) { goal ->
                GoalItem(goal = goal)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun GoalItem(goal: Goal) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = goal.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${goal.currentValue.toInt()} / ${goal.targetValue.toInt()} ${goal.unit}",
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = (goal.currentValue / goal.targetValue).coerceIn(0f, 1f),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
