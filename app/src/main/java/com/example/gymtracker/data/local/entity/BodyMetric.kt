package com.example.gymtracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "body_metrics")
data class BodyMetric(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long,
    val weight: Float,
    val bodyFatPercentage: Float?,
    val muscleMass: Float?
)
