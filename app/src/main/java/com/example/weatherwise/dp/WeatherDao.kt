package com.example.weatherwise.dp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherwise.model.Alert
import com.example.weatherwise.model.WeatherResponse
import kotlinx.coroutines.flow.Flow


@Dao
interface WeatherDao {


    @Query("SELECT * from weather_table")
    fun getWeatherResponse() : Flow<WeatherResponse>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWeatherResponse(weatherResponse: WeatherResponse)

//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    suspend fun insertAlert(alert: Alert)
//
//    @Query("SELECT * from alert_table")
//     fun getAlerts(): Flow<List<Alert>>
//
//   @Delete
//   suspend fun deleteAlert(alert: Alert)
}