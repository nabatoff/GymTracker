package com.example.gymtracker.util

import android.content.Context
import android.net.Uri
import com.example.gymtracker.data.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

@Serializable
data class ExportData(
    val sessions: List<WorkoutSessionExport>,
    val metrics: List<BodyMetricExport>,
    val programs: List<WorkoutProgramExport>,
    val exercises: List<ExerciseExport>,
    val goals: List<GoalExport>,
    val exportDate: String
)

@Serializable
data class WorkoutSessionExport(
    val id: Long,
    val startTime: Long,
    val endTime: Long?,
    val durationInMinutes: Int?
)

@Serializable
data class BodyMetricExport(
    val id: Long,
    val date: Long,
    val weight: Float,
    val bodyFatPercentage: Float?,
    val muscleMass: Float?
)

@Serializable
data class WorkoutProgramExport(
    val programId: Long,
    val name: String
)

@Serializable
data class ExerciseExport(
    val exerciseId: Long,
    val programId: Long,
    val name: String,
    val sets: Int,
    val reps: String
)

@Serializable
data class GoalExport(
    val goalId: Long,
    val title: String,
    val targetValue: Float,
    val currentValue: Float,
    val unit: String
)

class DataExporter(private val context: Context) {
    
    suspend fun exportToJson(
        sessions: List<WorkoutSession>,
        metrics: List<BodyMetric>,
        programs: List<WorkoutProgram>,
        exercises: List<Exercise>,
        goals: List<Goal>
    ): String = withContext(Dispatchers.IO) {
        val exportData = ExportData(
            sessions = sessions.map { 
                WorkoutSessionExport(it.id, it.startTime, it.endTime, it.durationInMinutes) 
            },
            metrics = metrics.map { 
                BodyMetricExport(it.id, it.date, it.weight, it.bodyFatPercentage, it.muscleMass) 
            },
            programs = programs.map { 
                WorkoutProgramExport(it.programId, it.name) 
            },
            exercises = exercises.map { 
                ExerciseExport(it.exerciseId, it.programId, it.name, it.sets, it.reps) 
            },
            goals = goals.map { 
                GoalExport(it.goalId, it.title, it.targetValue, it.currentValue, it.unit) 
            },
            exportDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        )
        
        Json { prettyPrint = true }.encodeToString(exportData)
    }
    
    suspend fun exportToCsv(
        sessions: List<WorkoutSession>,
        metrics: List<BodyMetric>,
        programs: List<WorkoutProgram>,
        exercises: List<Exercise>,
        goals: List<Goal>
    ): String = withContext(Dispatchers.IO) {
        val sb = StringBuilder()
        
        // Sessions
        sb.appendLine("=== WORKOUT SESSIONS ===")
        sb.appendLine("ID,Start Time,End Time,Duration (minutes)")
        sessions.forEach { session ->
            sb.appendLine("${session.id},${session.startTime},${session.endTime ?: ""},${session.durationInMinutes ?: ""}")
        }
        sb.appendLine()
        
        // Metrics
        sb.appendLine("=== BODY METRICS ===")
        sb.appendLine("ID,Date,Weight,Body Fat %,Muscle Mass")
        metrics.forEach { metric ->
            sb.appendLine("${metric.id},${metric.date},${metric.weight},${metric.bodyFatPercentage ?: ""},${metric.muscleMass ?: ""}")
        }
        sb.appendLine()
        
        // Programs
        sb.appendLine("=== WORKOUT PROGRAMS ===")
        sb.appendLine("ID,Name")
        programs.forEach { program ->
            sb.appendLine("${program.programId},${program.name}")
        }
        sb.appendLine()
        
        // Exercises
        sb.appendLine("=== EXERCISES ===")
        sb.appendLine("ID,Program ID,Name,Sets,Reps")
        exercises.forEach { exercise ->
            sb.appendLine("${exercise.exerciseId},${exercise.programId},${exercise.name},${exercise.sets},${exercise.reps}")
        }
        sb.appendLine()
        
        // Goals
        sb.appendLine("=== GOALS ===")
        sb.appendLine("ID,Title,Target Value,Current Value,Unit")
        goals.forEach { goal ->
            sb.appendLine("${goal.goalId},${goal.title},${goal.targetValue},${goal.currentValue},${goal.unit}")
        }
        
        sb.toString()
    }
    
    suspend fun saveToFile(content: String, filename: String): Uri? = withContext(Dispatchers.IO) {
        try {
            val file = File(context.getExternalFilesDir(null), filename)
            FileWriter(file).use { it.write(content) }
            Uri.fromFile(file)
        } catch (e: Exception) {
            null
        }
    }
}
