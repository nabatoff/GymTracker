package com.example.gymtracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true)
    val goalId: Long = 0,
    val title: String,
    val targetValue: Float,
    val currentValue: Float,
    val unit: String
)
