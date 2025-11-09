package com.gymtracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.gymtracker.data.repository.AppRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryViewModel(private val repository: AppRepository) : ViewModel() {
    val sessions = repository.getAllSessions()

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("ru"))
        return sdf.format(Date(timestamp))
    }

    fun formatDuration(minutes: Int): String {
        val hours = minutes / 60
        val mins = minutes % 60
        return if (hours > 0) {
            "$hours ч $mins мин"
        } else {
            "$mins мин"
        }
    }
}
