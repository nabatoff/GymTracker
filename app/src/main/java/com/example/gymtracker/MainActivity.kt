package com.example.gymtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.gymtracker.ui.viewmodel.GymTrackerViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = (application as GymTrackerApplication).appRepository
        val factory = GymTrackerViewModelFactory(repository)

        setContent {
            GymTrackerApp(viewModelFactory = factory)
        }
    }
}
