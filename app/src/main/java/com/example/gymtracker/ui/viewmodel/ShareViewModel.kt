package com.example.gymtracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtracker.data.repository.AppRepository
import com.example.gymtracker.ui.share.ProgressSummary
import kotlinx.coroutines.flow.*
import java.util.*

class ShareViewModel(private val repository: AppRepository) : ViewModel() {
    
    val progressSummary: StateFlow<ProgressSummary> = combine(
        repository.getAllSessions(),
        repository.getAllMetrics()
    ) { sessions, metrics ->
        val totalWorkouts = sessions.size
        
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfMonth = calendar.timeInMillis
        
        calendar.add(Calendar.MONTH, 1)
        val endOfMonth = calendar.timeInMillis
        
        val workoutsThisMonth = sessions.count { 
            it.startTime >= startOfMonth && it.startTime < endOfMonth 
        }
        
        val totalMinutes = sessions.sumOf { it.durationInMinutes?.toLong() ?: 0L }
        val totalHours = (totalMinutes / 60).toInt()
        
        val currentWeight = metrics.firstOrNull()?.weight
        
        ProgressSummary(
            totalWorkouts = totalWorkouts,
            workoutsThisMonth = workoutsThisMonth,
            totalHours = totalHours,
            currentWeight = currentWeight
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProgressSummary()
    )
}
