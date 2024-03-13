package com.example.weatherwise.model

import kotlinx.coroutines.flow.Flow

interface WeatherRepo {

     fun getCurrentWeather(lat: String,
                                  lon: String,
                                  language: String,
                                  units: String):Flow<WeatherResponse>
}