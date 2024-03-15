package com.example.weatherwise.dp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherwise.model.Alert


@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAlert(alert: Alert)

    @Query("SELECT * from alerts_table")
    suspend fun getAlert(): List<Alert>

    @Delete
    suspend fun deleteAlert(alert: Alert)
}