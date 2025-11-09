package com.example.gymtracker.ui.progress

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gymtracker.data.repository.AppRepository
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.FloatEntry
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProgressScreen(
    repository: AppRepository,
    viewModel: ProgressViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return ProgressViewModel(repository) as T
            }
        }
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    var showMetricDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showMetricDialog = true }) {
                Text("+")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Ввод данных",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Последние измерения:",
                            style = MaterialTheme.typography.titleMedium
                        )
                        val latestMetric = uiState.metrics.firstOrNull()
                        if (latestMetric != null) {
                            Text("Вес: ${latestMetric.weight} кг")
                            latestMetric.bodyFatPercentage?.let {
                                Text("Жир: $it%")
                            }
                            latestMetric.muscleMass?.let {
                                Text("Мышцы: $it кг")
                            }
                        } else {
                            Text("Нет данных")
                        }
                    }
                }
            }

            item {
                Text(
                    text = "График веса",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            item {
                if (uiState.metrics.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    ) {
                        val chartEntries = uiState.metrics
                            .sortedBy { it.date }
                            .mapIndexed { index, metric -> 
                                FloatEntry(index.toFloat(), metric.weight)
                            }

                        if (chartEntries.isNotEmpty()) {
                            Chart(
                                chart = lineChart(),
                                model = entryModelOf(chartEntries),
                                startAxis = rememberStartAxis(),
                                bottomAxis = rememberBottomAxis()
                            )
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Нет данных для графика")
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Цели",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            items(uiState.goals) { goal ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = goal.title,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "${goal.currentValue.toInt()}/${goal.targetValue.toInt()} ${goal.unit}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        LinearProgressIndicator(
                            progress = {
                                if (goal.targetValue > 0) {
                                    (goal.currentValue / goal.targetValue).coerceIn(0f, 1f)
                                } else {
                                    0f
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }

    if (showMetricDialog) {
        AddMetricDialog(
            onDismiss = { showMetricDialog = false },
            onConfirm = { weight, bodyFat, muscleMass ->
                viewModel.saveBodyMetric(weight, bodyFat, muscleMass)
                showMetricDialog = false
            }
        )
    }
}

@Composable
fun AddMetricDialog(
    onDismiss: () -> Unit,
    onConfirm: (Float, Float?, Float?) -> Unit
) {
    var weight by remember { mutableStateOf("") }
    var bodyFat by remember { mutableStateOf("") }
    var muscleMass by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новое измерение") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Вес (кг) *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = bodyFat,
                    onValueChange = { bodyFat = it },
                    label = { Text("Процент жира (%)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = muscleMass,
                    onValueChange = { muscleMass = it },
                    label = { Text("Мышечная масса (кг)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val weightFloat = weight.toFloatOrNull()
                    if (weightFloat != null && weightFloat > 0) {
                        val bodyFatFloat = bodyFat.toFloatOrNull()
                        val muscleMassFloat = muscleMass.toFloatOrNull()
                        onConfirm(weightFloat, bodyFatFloat, muscleMassFloat)
                    }
                }
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}
