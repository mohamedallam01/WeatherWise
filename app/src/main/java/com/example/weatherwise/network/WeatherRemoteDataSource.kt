package com.example.weatherwise.network

import com.example.weatherwise.model.FavoriteWeather
import com.example.weatherwise.model.WeatherResponse
import retrofit2.Response

interface WeatherRemoteDataSource {

    suspend fun getCurrentWeather(
        lat : String,
        lon : String,
        language : String,
        units : String
    ) : WeatherResponse
}