package com.example.gymtracker.data.dao

import androidx.room.*
import com.example.gymtracker.data.entities.BodyMetric
import kotlinx.coroutines.flow.Flow

@Dao
interface BodyMetricDao {
    @Query("SELECT * FROM body_metrics ORDER BY date DESC")
    fun getAllMetricsSortedByDate(): Flow<List<BodyMetric>>

    @Query("SELECT * FROM body_metrics WHERE id = :id")
    suspend fun getMetricById(id: Long): BodyMetric?

    @Query("SELECT * FROM body_metrics ORDER BY date DESC LIMIT 1")
    suspend fun getLatestMetric(): BodyMetric?

    @Insert
    suspend fun insertMetric(metric: BodyMetric): Long

    @Update
    suspend fun updateMetric(metric: BodyMetric)

    @Delete
    suspend fun deleteMetric(metric: BodyMetric)
}
