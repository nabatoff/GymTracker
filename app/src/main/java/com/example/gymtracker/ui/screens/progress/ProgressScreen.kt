package com.example.gymtracker.ui.screens.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.gymtracker.R
import com.example.gymtracker.ui.viewmodel.ProgressViewModel
import kotlin.math.max

@Composable
fun ProgressScreen(
    viewModel: ProgressViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val formState by viewModel.formState.collectAsState()

    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.add_metric_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = formState.weight,
                onValueChange = { viewModel.onWeightChange(it) },
                label = { Text(stringResource(id = R.string.weight_hint)) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = formState.bodyFat,
                    onValueChange = { viewModel.onBodyFatChange(it) },
                    label = { Text(stringResource(id = R.string.body_fat_hint)) },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = formState.muscleMass,
                    onValueChange = { viewModel.onMuscleMassChange(it) },
                    label = { Text(stringResource(id = R.string.muscle_mass_hint)) },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
            Button(
                onClick = { viewModel.saveMetric() },
                modifier = Modifier
                    .padding(top = 12.dp)
                    .align(Alignment.End)
            ) {
                Text(text = stringResource(id = R.string.save))
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(id = R.string.weight_chart_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            WeightChart(entries = uiState.weightEntries, modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
                .height(200.dp))

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(id = R.string.recent_metrics_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            if (uiState.metrics.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.no_metrics),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            } else {
                uiState.metrics.takeLast(5).reversed().forEach { metric ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        colors = CardDefaults.cardColors()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = stringResource(id = R.string.weight_label, metric.weight))
                            metric.bodyFatPercentage?.let { fat ->
                                Text(text = stringResource(id = R.string.body_fat_label, fat))
                            }
                            metric.muscleMass?.let { muscle ->
                                Text(text = stringResource(id = R.string.muscle_mass_label, muscle))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(id = R.string.goals_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            if (uiState.goals.isEmpty()) {
                Text(text = stringResource(id = R.string.no_goals), modifier = Modifier.padding(top = 8.dp))
            } else {
                uiState.goals.forEach { goal ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = goal.title, style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = stringResource(
                                    id = R.string.goal_progress_value,
                                    goal.currentValue,
                                    goal.targetValue,
                                    goal.unit
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            LinearProgressIndicator(
                                progress = goal.progress,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeightChart(entries: List<Pair<Long, Float>>, modifier: Modifier = Modifier) {
    if (entries.size < 2) {
        Card(
            modifier = modifier,
            colors = CardDefaults.cardColors()
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = if (entries.isEmpty()) {
                        stringResource(id = R.string.no_data)
                    } else {
                        stringResource(id = R.string.add_more_measurements)
                    }
                )
            }
        }
        return
    }

    val weights = entries.map { it.second }
    val minWeight = weights.minOrNull() ?: 0f
    val maxWeight = weights.maxOrNull() ?: 0f
    val weightRange = max(1f, maxWeight - minWeight)

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors()
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val xStep = if (entries.size == 1) 0f else size.width / (entries.size - 1)
            val path = Path()
            entries.forEachIndexed { index, entry ->
                val normalized = (entry.second - minWeight) / weightRange
                val x = xStep * index
                val y = size.height - (normalized * size.height)
                val point = Offset(x, y)
                if (index == 0) {
                    path.moveTo(point.x, point.y)
                } else {
                    path.lineTo(point.x, point.y)
                }
            }

            drawPath(
                path = path,
                color = MaterialTheme.colorScheme.primary,
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            )

            entries.forEachIndexed { index, entry ->
                val normalized = (entry.second - minWeight) / weightRange
                val x = xStep * index
                val y = size.height - (normalized * size.height)
                drawCircle(
                    color = MaterialTheme.colorScheme.primary,
                    radius = 6.dp.toPx(),
                    center = Offset(x, y)
                )
            }
        }
    }
}
