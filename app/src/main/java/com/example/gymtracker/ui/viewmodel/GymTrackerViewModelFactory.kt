package com.example.gymtracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gymtracker.data.repository.AppRepository

class GymTrackerViewModelFactory(
    private val repository: AppRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(repository) as T
        modelClass.isAssignableFrom(ProgramsViewModel::class.java) -> ProgramsViewModel(repository) as T
        modelClass.isAssignableFrom(ProgressViewModel::class.java) -> ProgressViewModel(repository) as T
        modelClass.isAssignableFrom(HistoryViewModel::class.java) -> HistoryViewModel(repository) as T
        else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
