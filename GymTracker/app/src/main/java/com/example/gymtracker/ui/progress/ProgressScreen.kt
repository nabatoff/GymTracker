package com.example.gymtracker.ui.progress

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gymtracker.data.local.entities.BodyMetric
import com.example.gymtracker.data.repository.GoalProgress
import com.example.gymtracker.ui.components.LineChart
import com.example.gymtracker.util.formatTimestamp
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ProgressScreen(
    metricsState: StateFlow<List<BodyMetric>>,
    goalProgressState: StateFlow<List<GoalProgress>>,
    isSavingState: StateFlow<Boolean>,
    onSaveMetric: (Float, Float?, Float?) -> Unit
) {
    val metrics by metricsState.collectAsStateWithLifecycle()
    val goals by goalProgressState.collectAsStateWithLifecycle()
    val isSaving by isSavingState.collectAsStateWithLifecycle()

    var weightText by rememberSaveable { mutableStateOf("") }
    var bodyFatText by rememberSaveable { mutableStateOf("") }
    var muscleText by rememberSaveable { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Новая метрика тела",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    OutlinedTextField(
                        value = weightText,
                        onValueChange = { weightText = it },
                        label = { Text("Вес (кг)") },
                        placeholder = { Text("80.5") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = bodyFatText,
                        onValueChange = { bodyFatText = it },
                        label = { Text("% жира") },
                        placeholder = { Text("15.2") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = muscleText,
                        onValueChange = { muscleText = it },
                        label = { Text("Мышечная масса (кг)") },
                        placeholder = { Text("40.0") },
                        singleLine = true
                    )
                    Button(
                        onClick = {
                            val weight = weightText.replace(",", ".").toFloatOrNull()
                            val fat = bodyFatText.replace(",", ".").toFloatOrNull()
                            val muscle = muscleText.replace(",", ".").toFloatOrNull()
                            if (weight != null && weight > 0f) {
                                onSaveMetric(weight, fat, muscle)
                                weightText = ""
                                bodyFatText = ""
                                muscleText = ""
                            }
                        },
                        enabled = !isSaving
                    ) {
                        Text("Сохранить")
                    }
                    if (isSaving) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Динамика веса",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    LineChart(points = metrics.map { it.date to it.weight })
                }
            }
        }

        if (goals.isNotEmpty()) {
            item {
                Text(
                    text = "Цели",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            items(goals, key = { it.goal.goalId }) { goal ->
                GoalCard(goal = goal)
            }
        }

        if (metrics.isNotEmpty()) {
            item {
                Text(
                    text = "История измерений",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            items(metrics, key = { it.id }) { metric ->
                BodyMetricRow(metric = metric)
            }
        }
    }
}

@Composable
private fun GoalCard(goal: GoalProgress) {
    val progress = if (goal.targetValue == 0f) 0f else (goal.currentValue / goal.targetValue).coerceIn(0f, 1.5f)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = goal.goal.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) }
            )
            Text(
                text = String.format(
                    "%.1f / %.1f %s",
                    goal.currentValue,
                    goal.targetValue,
                    goal.goal.unit
                ),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun BodyMetricRow(metric: BodyMetric) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = formatTimestamp(metric.date),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(text = "Вес: ${String.format("%.1f", metric.weight)} кг")
            metric.bodyFatPercentage?.let {
                Text(text = "Жир: ${String.format("%.1f", it)} %")
            }
            metric.muscleMass?.let {
                Text(text = "Мышцы: ${String.format("%.1f", it)} кг")
            }
        }
    }
}
