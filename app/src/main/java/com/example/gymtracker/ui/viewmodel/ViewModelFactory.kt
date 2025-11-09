package com.example.gymtracker.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gymtracker.data.repository.AppRepository
import com.example.gymtracker.util.PreferencesManager

class ViewModelFactory(
    private val repository: AppRepository,
    private val preferencesManager: PreferencesManager,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(ProgramsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProgramsViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(ProgressViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProgressViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoryViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(repository, preferencesManager, context) as T
        }
        if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalendarViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(AdvancedStatsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdvancedStatsViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(ShareViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ShareViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
