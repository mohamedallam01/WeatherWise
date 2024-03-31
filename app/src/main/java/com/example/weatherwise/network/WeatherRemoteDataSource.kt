package com.example.weatherwise.network

import com.example.weatherwise.model.entities.WeatherResponse

interface WeatherRemoteDataSource {

    suspend fun getCurrentWeather(
        lat : String,
        lon : String,
        language : String,
        units : String
    ) : WeatherResponse
}