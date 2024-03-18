package com.example.weatherwise.dp

import com.example.weatherwise.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSource {

   fun getWeatherResponse() : Flow<WeatherResponse>
   suspend fun insertWeatherResponse(weatherResponse: WeatherResponse)
}