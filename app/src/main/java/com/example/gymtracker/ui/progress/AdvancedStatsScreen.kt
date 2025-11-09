package com.example.gymtracker.ui.progress

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymtracker.ui.viewmodel.AdvancedStatsViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedStatsScreen(
    viewModel: AdvancedStatsViewModel = viewModel()
) {
    val stats by viewModel.stats.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Расширенная статистика",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                StatCard(
                    title = "Средняя длительность тренировки",
                    value = "${stats.avgWorkoutDuration} мин"
                )
            }
            
            item {
                StatCard(
                    title = "Всего времени в зале",
                    value = "${stats.totalHours} часов"
                )
            }
            
            item {
                StatCard(
                    title = "Тренировок в неделю",
                    value = "${stats.workoutsPerWeek}"
                )
            }
            
            item {
                StatCard(
                    title = "Изменение веса за месяц",
                    value = if (stats.weightChange >= 0) "+${stats.weightChange} кг" else "${stats.weightChange} кг"
                )
            }
            
            item {
                ComparisonCard(
                    title = "Сравнение с прошлым месяцем",
                    current = stats.currentMonthWorkouts,
                    previous = stats.previousMonthWorkouts,
                    label = "тренировок"
                )
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ComparisonCard(title: String, current: Int, previous: Int, label: String) {
    val change = current - previous
    val percentage = if (previous > 0) {
        ((change.toFloat() / previous) * 100).toInt()
    } else 0

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Текущий месяц",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$current $label",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column {
                    Text(
                        text = "Прошлый месяц",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$previous $label",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column {
                    Text(
                        text = "Изменение",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${if (change >= 0) "+" else ""}$change ($percentage%)",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (change >= 0) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
