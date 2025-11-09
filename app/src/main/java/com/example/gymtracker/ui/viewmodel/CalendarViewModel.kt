package com.example.gymtracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtracker.data.repository.AppRepository
import kotlinx.coroutines.flow.*
import java.util.*

class CalendarViewModel(private val repository: AppRepository) : ViewModel() {
    private val _selectedDate = MutableStateFlow(Date())
    val selectedDate: StateFlow<Date> = _selectedDate.asStateFlow()

    fun selectDate(date: Date) {
        _selectedDate.value = date
    }

    fun getWorkoutsForMonth(date: Date): Flow<List<com.example.gymtracker.ui.calendar.WorkoutCalendarItem>> {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfMonth = calendar.timeInMillis
        
        calendar.add(Calendar.MONTH, 1)
        val endOfMonth = calendar.timeInMillis

        return repository.getAllSessions()
            .map { sessions ->
                sessions.filter { 
                    it.startTime >= startOfMonth && it.startTime < endOfMonth 
                }.map { session ->
                    com.example.gymtracker.ui.calendar.WorkoutCalendarItem(
                        date = Date(session.startTime),
                        duration = session.durationInMinutes ?: 0,
                        programName = null // Можно добавить связь с программой
                    )
                }
            }
    }
}
