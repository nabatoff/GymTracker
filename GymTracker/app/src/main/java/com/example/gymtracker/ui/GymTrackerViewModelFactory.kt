package com.example.gymtracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gymtracker.data.repository.AppRepository
import com.example.gymtracker.ui.history.HistoryViewModel
import com.example.gymtracker.ui.home.HomeViewModel
import com.example.gymtracker.ui.progress.ProgressViewModel
import com.example.gymtracker.ui.programs.ProgramsViewModel

class GymTrackerViewModelFactory(
    private val repository: AppRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(repository)
            modelClass.isAssignableFrom(ProgramsViewModel::class.java) -> ProgramsViewModel(repository)
            modelClass.isAssignableFrom(ProgressViewModel::class.java) -> ProgressViewModel(repository)
            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> HistoryViewModel(repository)
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        } as T
    }
}
