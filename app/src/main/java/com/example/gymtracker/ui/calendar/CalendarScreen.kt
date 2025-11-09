package com.example.gymtracker.ui.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymtracker.ui.viewmodel.CalendarViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = viewModel()
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val workouts by viewModel.getWorkoutsForMonth(selectedDate).collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Календарь тренировок",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Простой календарь
        CalendarView(
            selectedDate = selectedDate,
            workouts = workouts,
            onDateSelected = { viewModel.selectDate(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Тренировки в этом месяце",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn {
            items(workouts) { workout ->
                WorkoutCalendarItem(workout = workout)
            }
        }
    }
}

@Composable
fun CalendarView(
    selectedDate: Date,
    workouts: List<WorkoutCalendarItem>,
    onDateSelected: (Date) -> Unit
) {
    val calendar = Calendar.getInstance()
    calendar.time = selectedDate
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    
    val workoutDates = workouts.map { 
        val cal = Calendar.getInstance()
        cal.time = it.date
        cal.get(Calendar.DAY_OF_MONTH)
    }.toSet()

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = SimpleDateFormat("MMMM yyyy", Locale("ru")).format(selectedDate),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Дни недели
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс").forEach { day ->
                    Text(
                        text = day,
                        fontSize = 12.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            // Календарная сетка
            var currentDay = 1
            repeat(6) { week ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    repeat(7) { dayOfWeek ->
                        if (week == 0 && dayOfWeek < firstDayOfWeek - 1) {
                            Box(modifier = Modifier.weight(1f))
                        } else if (currentDay <= daysInMonth) {
                            val day = currentDay
                            val hasWorkout = workoutDates.contains(day)
                            val isSelected = Calendar.getInstance().apply {
                                time = selectedDate
                            }.get(Calendar.DAY_OF_MONTH) == day
                            
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                            ) {
                                Surface(
                                    onClick = {
                                        val cal = Calendar.getInstance()
                                        cal.time = selectedDate
                                        cal.set(Calendar.DAY_OF_MONTH, day)
                                        onDateSelected(cal.time)
                                    },
                                    color = when {
                                        isSelected -> MaterialTheme.colorScheme.primary
                                        hasWorkout -> MaterialTheme.colorScheme.secondaryContainer
                                        else -> MaterialTheme.colorScheme.surface
                                    },
                                    shape = MaterialTheme.shapes.small
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = androidx.compose.ui.Alignment.Center
                                    ) {
                                        Text(
                                            text = day.toString(),
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                            currentDay++
                        } else {
                            Box(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

data class WorkoutCalendarItem(
    val date: Date,
    val duration: Int,
    val programName: String?
)

@Composable
fun WorkoutCalendarItem(workout: WorkoutCalendarItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = SimpleDateFormat("dd MMMM yyyy", Locale("ru")).format(workout.date),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                if (workout.programName != null) {
                    Text(
                        text = workout.programName,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = "${workout.duration} мин",
                fontSize = 14.sp
            )
        }
    }
}
