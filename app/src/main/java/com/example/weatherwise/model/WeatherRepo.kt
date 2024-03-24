package com.example.weatherwise.model

import kotlinx.coroutines.flow.Flow

interface WeatherRepo {

            fun getWeatherResponse() : Flow<WeatherResponse>
           // suspend fun insertWeatherResponse(weatherResponse: WeatherResponse)

     fun getCurrentWeatherFromRemote(lat: String,
                                  lon: String,
                                  language: String,
                                  units: String):Flow<WeatherResponse>


     fun getAllAlerts() :Flow<List<Alert>>
     fun getAllFavorites() :Flow<List<FavoriteWeather>>

     suspend fun insertAlert(alert: Alert?)
     suspend fun insertFavorite(favoriteWeather: FavoriteWeather)
     suspend fun deleteAlert(alert: Alert)

     fun getFavoriteById(favoriteId : Int) :Flow<FavoriteWeather>
}