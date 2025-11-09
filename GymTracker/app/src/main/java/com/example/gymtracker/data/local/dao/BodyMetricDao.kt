package com.example.gymtracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gymtracker.data.local.entities.BodyMetric
import kotlinx.coroutines.flow.Flow

@Dao
interface BodyMetricDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(metric: BodyMetric): Long

    @Update
    suspend fun update(metric: BodyMetric)

    @Delete
    suspend fun delete(metric: BodyMetric)

    @Query("SELECT * FROM body_metrics WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): BodyMetric?

    @Query("SELECT * FROM body_metrics ORDER BY date DESC")
    fun getAllMetricsSortedByDate(): Flow<List<BodyMetric>>

    @Query("SELECT * FROM body_metrics ORDER BY date DESC LIMIT 1")
    suspend fun getLatestMetric(): BodyMetric?
}
