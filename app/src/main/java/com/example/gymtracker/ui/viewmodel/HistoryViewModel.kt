package com.example.gymtracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtracker.data.local.entity.WorkoutSession
import com.example.gymtracker.data.repository.AppRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HistoryViewModel(repository: AppRepository) : ViewModel() {

    val sessions: StateFlow<List<WorkoutSession>> = repository
        .getAllWorkoutSessionsSorted()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
}
