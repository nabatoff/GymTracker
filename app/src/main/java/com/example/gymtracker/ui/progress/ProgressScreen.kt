package com.example.gymtracker.ui.progress

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymtracker.data.entity.Goal
import com.example.gymtracker.ui.viewmodel.ProgressViewModel
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    viewModel: ProgressViewModel = viewModel()
) {
    val goals by viewModel.goals.collectAsStateWithLifecycle(initialValue = emptyList())
    val weightData by viewModel.weightData.collectAsStateWithLifecycle(initialValue = emptyList())
    var showInputDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Прогресс",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = { showInputDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Ввести данные")
        }

        if (weightData.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 16.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    SimpleLineChart(data = weightData)
                }
            }
        }

        Text(
            text = "Цели",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(goals) { goal ->
                GoalCard(goal = goal)
            }
        }
    }

    if (showInputDialog) {
        BodyMetricInputDialog(
            onDismiss = { showInputDialog = false },
            onConfirm = { weight, bodyFat, muscleMass ->
                viewModel.saveBodyMetric(weight, bodyFat, muscleMass)
                showInputDialog = false
            }
        )
    }
}

@Composable
fun GoalCard(goal: Goal) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = goal.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
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
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${goal.currentValue.toInt()} / ${goal.targetValue.toInt()} ${goal.unit}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SimpleLineChart(data: List<Pair<Long, Float>>) {
    if (data.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Нет данных для графика")
        }
        return
    }

    val weights = data.map { it.second }
    val minWeight = weights.minOrNull() ?: 0f
    val maxWeight = weights.maxOrNull() ?: 100f
    val range = maxWeight - minWeight

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val padding = 40f

        val path = Path()
        val pointPath = Path()

        data.forEachIndexed { index, (_, weight) ->
            val x = padding + (width - 2 * padding) * (index.toFloat() / (data.size - 1).coerceAtLeast(1))
            val normalizedWeight = if (range > 0) (weight - minWeight) / range else 0.5f
            val y = height - padding - (height - 2 * padding) * normalizedWeight

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }

            pointPath.addOval(
                Offset(x - 4f, y - 4f),
                Size(8f, 8f)
            )
        }

        drawPath(
            path = path,
            color = Color.Blue,
            style = Stroke(width = 3f)
        )

        drawPath(
            path = pointPath,
            color = Color.Blue
        )
    }
}

@Composable
fun BodyMetricInputDialog(
    onDismiss: () -> Unit,
    onConfirm: (Float, Float?, Float?) -> Unit
) {
    var weight by remember { mutableStateOf("") }
    var bodyFat by remember { mutableStateOf("") }
    var muscleMass by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ввести данные") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Вес (кг)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = bodyFat,
                    onValueChange = { bodyFat = it },
                    label = { Text("Процент жира (%)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = muscleMass,
                    onValueChange = { muscleMass = it },
                    label = { Text("Мышечная масса (кг)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val weightFloat = weight.toFloatOrNull()
                    if (weightFloat != null) {
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
