package com.example.gymtracker.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun PullToRefreshBox(
    isRefreshing: Boolean,
    onRefresh: suspend () -> Unit,
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        content()
        if (isRefreshing) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

suspend fun refreshData() {
    delay(500) // Симуляция обновления данных
}
