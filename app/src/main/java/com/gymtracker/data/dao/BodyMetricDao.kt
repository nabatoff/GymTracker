package com.gymtracker.data.dao

import androidx.room.*
import com.gymtracker.data.entities.BodyMetric
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
    suspend fun insert(metric: BodyMetric)

    @Update
    suspend fun update(metric: BodyMetric)

    @Delete
    suspend fun delete(metric: BodyMetric)
}
