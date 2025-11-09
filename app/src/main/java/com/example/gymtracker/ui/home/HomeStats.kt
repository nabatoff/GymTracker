package com.example.gymtracker.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class HomeStats(
    val totalWorkouts: Int = 0,
    val workoutsThisMonth: Int = 0,
    val totalMinutes: Int = 0,
    val currentWeight: Float? = null,
    val lastWorkoutDate: String? = null
)

@Composable
fun HomeStatsCard(stats: HomeStats) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Статистика",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Всего тренировок",
                    value = stats.totalWorkouts.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    label = "В этом месяце",
                    value = stats.workoutsThisMonth.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Всего минут",
                    value = stats.totalMinutes.toString(),
                    modifier = Modifier.weight(1f)
                )
                if (stats.currentWeight != null) {
                    StatItem(
                        label = "Текущий вес",
                        value = "${stats.currentWeight.toInt()} кг",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            if (stats.lastWorkoutDate != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Последняя тренировка: ${stats.lastWorkoutDate}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun StatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
