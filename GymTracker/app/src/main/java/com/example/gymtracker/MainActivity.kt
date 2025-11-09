package com.example.gymtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.gymtracker.ui.GymTrackerViewModelFactory
import com.example.gymtracker.ui.navigation.GymTrackerAppRoot
import com.example.gymtracker.ui.theme.GymTrackerTheme

class MainActivity : ComponentActivity() {

    private val appContainer by lazy { (application as GymTrackerApp).container }
    private val viewModelFactory by lazy { GymTrackerViewModelFactory(appContainer.repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GymTrackerTheme {
                GymTrackerAppRoot(
                    viewModelFactory = viewModelFactory
                )
            }
        }
    }
}
