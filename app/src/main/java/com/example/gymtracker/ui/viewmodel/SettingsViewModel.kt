package com.example.gymtracker.ui.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtracker.data.repository.AppRepository
import com.example.gymtracker.util.DataExporter
import com.example.gymtracker.util.PreferencesManager
import com.example.gymtracker.util.NotificationScheduler
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: AppRepository,
    private val preferencesManager: PreferencesManager,
    private val context: Context
) : ViewModel() {
    
    val isDarkTheme: StateFlow<Boolean> = preferencesManager.isDarkTheme
    val exportInProgress = MutableStateFlow(false)

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setDarkTheme(enabled)
        }
    }

    suspend fun exportData(context: Context, format: String) {
        exportInProgress.value = true
        try {
            val sessions = repository.getAllSessions().first()
            val metrics = repository.getAllMetrics().first()
            val programs = repository.getAllPrograms().first()
            val exercises = programs.flatMap { program ->
                repository.getExercisesByProgramId(program.programId).first()
            }
            val goals = repository.getAllGoals().first()

            val exporter = DataExporter(context)
            val content = when (format) {
                "json" -> exporter.exportToJson(sessions, metrics, programs, exercises, goals)
                "csv" -> exporter.exportToCsv(sessions, metrics, programs, exercises, goals)
                else -> return
            }

            val filename = "gymtracker_export_${System.currentTimeMillis()}.$format"
            val uri = exporter.saveToFile(content, filename)
            
            uri?.let {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, it)
                    type = "text/*"
                }
                context.startActivity(Intent.createChooser(shareIntent, "Экспорт данных"))
            }
        } finally {
            exportInProgress.value = false
        }
    }

    fun backupToGoogleDrive(context: Context) {
        viewModelScope.launch {
            // Реализация будет в отдельном файле
            // GoogleDriveBackup.uploadBackup(context, repository)
        }
    }

    fun setNotificationTime(time: String) {
        viewModelScope.launch {
            preferencesManager.setNotificationTime(time)
            NotificationScheduler.scheduleWorkoutReminder(context, time)
        }
    }

    fun disableNotifications() {
        viewModelScope.launch {
            preferencesManager.setNotificationTime(null)
            NotificationScheduler.cancelWorkoutReminder(context)
        }
    }
}
