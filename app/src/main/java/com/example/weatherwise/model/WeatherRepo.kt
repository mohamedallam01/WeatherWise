package com.example.weatherwise.model

import kotlinx.coroutines.flow.Flow

interface WeatherRepo {

            fun getWeatherResponse() : Flow<WeatherResponse>
           // suspend fun insertWeatherResponse(weatherResponse: WeatherResponse)

     fun getCurrentWeatherFromRemote(lat: String,
                                  lon: String,
                                  language: String,
                                  units: String):Flow<WeatherResponse>
}