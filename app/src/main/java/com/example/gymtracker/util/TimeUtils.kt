package com.example.gymtracker.util

import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val dateFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm", Locale.getDefault())

fun formatTimestamp(timestamp: Long): String =
    Instant.ofEpochMilli(timestamp)
        .atZone(ZoneId.systemDefault())
        .format(dateFormatter)

fun formatDurationMinutes(minutes: Int): String {
    val duration = Duration.ofMinutes(minutes.toLong())
    val hours = duration.toHours()
    val remMinutes = duration.minusHours(hours).toMinutes()
    return if (hours > 0) {
        String.format(Locale.getDefault(), \"%d ч %d мин\", hours, remMinutes)
    } else {
        String.format(Locale.getDefault(), \"%d мин\", remMinutes)
    }
}

fun formatElapsedTime(elapsedMillis: Long): String {
    val totalSeconds = elapsedMillis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
}
