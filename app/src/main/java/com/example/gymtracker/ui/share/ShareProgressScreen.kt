package com.example.gymtracker.ui.share

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymtracker.ui.viewmodel.ShareViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareProgressScreen(
    viewModel: ShareViewModel = viewModel()
) {
    val context = LocalContext.current
    val progressSummary by viewModel.progressSummary.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Поделиться прогрессом",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Мой прогресс в GymTracker",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Всего тренировок: ${progressSummary.totalWorkouts}",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    text = "Тренировок в этом месяце: ${progressSummary.workoutsThisMonth}",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    text = "Всего времени в зале: ${progressSummary.totalHours} часов",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                if (progressSummary.currentWeight != null) {
                    Text(
                        text = "Текущий вес: ${progressSummary.currentWeight} кг",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val shareText = buildShareText(progressSummary)
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    type = "text/plain"
                }
                context.startActivity(Intent.createChooser(sendIntent, "Поделиться прогрессом"))
            },
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp)
        ) {
            Icon(Icons.Default.Share, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Поделиться прогрессом")
        }
    }
}

fun buildShareText(summary: ProgressSummary): String {
    return buildString {
        appendLine("🏋️ Мой прогресс в GymTracker!")
        appendLine()
        appendLine("📊 Статистика:")
        appendLine("• Всего тренировок: ${summary.totalWorkouts}")
        appendLine("• Тренировок в этом месяце: ${summary.workoutsThisMonth}")
        appendLine("• Всего времени в зале: ${summary.totalHours} часов")
        if (summary.currentWeight != null) {
            appendLine("• Текущий вес: ${summary.currentWeight} кг")
        }
        appendLine()
        appendLine("#GymTracker #Фитнес #Прогресс")
    }
}

data class ProgressSummary(
    val totalWorkouts: Int = 0,
    val workoutsThisMonth: Int = 0,
    val totalHours: Int = 0,
    val currentWeight: Float? = null
)
