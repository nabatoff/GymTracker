package com.example.gymtracker.util

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit
import java.util.Calendar

class NotificationScheduler {
    companion object {
        private const val WORK_NAME = "workout_reminder"

        fun scheduleWorkoutReminder(context: Context, time: String) {
            val (hour, minute) = time.split(":").map { it.toInt() }
            
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                
                // Если время уже прошло сегодня, планируем на завтра
                if (timeInMillis < System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_MONTH, 1)
                }
            }

            val delay = calendar.timeInMillis - System.currentTimeMillis()

            val workRequest = PeriodicWorkRequestBuilder<WorkoutReminderWorker>(
                1, TimeUnit.DAYS
            )
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                        .build()
                )
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )
        }

        fun cancelWorkoutReminder(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}

class WorkoutReminderWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    override fun doWork(): Result {
        // Отправка уведомления будет реализована через NotificationManager
        return Result.success()
    }
}
