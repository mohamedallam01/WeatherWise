package com.example.weatherwise.dp

import com.example.weatherwise.model.entities.Alert
import com.example.weatherwise.model.entities.FavoriteWeather
import com.example.weatherwise.model.entities.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSource {

   fun getWeatherResponse() : Flow<WeatherResponse>
   suspend fun insertWeatherResponse(weatherResponse: WeatherResponse)


   suspend fun insertAlert(alert: Alert?)
   suspend fun insertFavorite(favoriteWeather: FavoriteWeather)
   fun getAllAlerts() : Flow<List<Alert>>
   fun getAllFavorites() : Flow<List<FavoriteWeather>>

   fun getFavoriteById(favoriteId : Int) : Flow<FavoriteWeather>

   suspend fun deleteAlert(alert: Alert?)
   suspend fun deleteFavorite(favoriteWeather: FavoriteWeather)

}