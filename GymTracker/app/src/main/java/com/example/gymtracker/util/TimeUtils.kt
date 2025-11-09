package com.example.gymtracker.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

private val dateFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm").withZone(ZoneId.systemDefault())

fun formatDuration(totalSeconds: Long): String {
    val hours = TimeUnit.SECONDS.toHours(totalSeconds)
    val minutes = TimeUnit.SECONDS.toMinutes(totalSeconds) % 60
    val seconds = totalSeconds % 60
    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}

fun formatMinutesToReadable(minutes: Int): String {
    val hoursPart = minutes / 60
    val minutesPart = minutes % 60
    return when {
        hoursPart > 0 && minutesPart > 0 -> "${hoursPart} ч ${minutesPart} мин"
        hoursPart > 0 -> "${hoursPart} ч"
        else -> "${minutesPart} мин"
    }
}

fun formatTimestamp(timestamp: Long): String =
    dateFormatter.format(Instant.ofEpochMilli(timestamp))
