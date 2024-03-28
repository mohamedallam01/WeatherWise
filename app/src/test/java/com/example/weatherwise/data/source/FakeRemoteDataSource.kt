package com.example.weatherwise.data.source

import com.example.weatherwise.model.WeatherResponse
import com.example.weatherwise.network.WeatherRemoteDataSource

class FakeRemoteDataSource(private val weatherResponse: WeatherResponse) : WeatherRemoteDataSource {
    override suspend fun getCurrentWeather(
        lat: String,
        lon: String,
        language: String,
        units: String
    ): WeatherResponse {
        return weatherResponse
    }
}