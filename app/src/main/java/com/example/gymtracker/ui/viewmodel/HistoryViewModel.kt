package com.example.gymtracker.ui.viewmodel

import com.example.gymtracker.data.repository.AppRepository

class HistoryViewModel(private val repository: AppRepository) {
    val sessions = repository.getAllSessions()
}
