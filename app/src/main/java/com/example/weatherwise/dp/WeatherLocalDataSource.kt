package com.example.weatherwise.dp

import com.example.weatherwise.model.Alert
import com.example.weatherwise.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSource {

   fun getWeatherResponse() : Flow<WeatherResponse>
   suspend fun insertWeatherResponse(weatherResponse: WeatherResponse)


   suspend fun insertAlert(alert: Alert)
   fun getAllAlerts() : Flow<List<Alert>>

   suspend fun deleteAlert(alert: Alert)

}