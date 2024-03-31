package com.example.weatherwise.dp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherwise.model.entities.Alert
import com.example.weatherwise.model.entities.FavoriteWeather
import com.example.weatherwise.model.entities.WeatherResponse
import kotlinx.coroutines.flow.Flow


@Dao
interface WeatherDao {


    @Query("SELECT * from weather_table")
    fun getWeatherResponse() : Flow<WeatherResponse>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWeatherResponse(weatherResponse: WeatherResponse)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAlert(alert: Alert)


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavorite(favoriteWeather: FavoriteWeather)

    @Query("SELECT * from favorite_table")
    fun getAllFavorites() : Flow<List<FavoriteWeather>>

    @Query("SELECT * FROM favorite_table WHERE fav_id = :favoriteId")
    fun getFavoriteById(favoriteId : Int): Flow<FavoriteWeather>

    @Query("SELECT * from alert_table")
     fun getAlerts(): Flow<List<Alert>>

   @Delete
   suspend fun deleteAlert(alert: Alert)

    @Delete
    suspend fun deleteFavorite(favoriteWeather: FavoriteWeather)
}