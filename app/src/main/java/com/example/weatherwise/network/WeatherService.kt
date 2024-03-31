package com.example.weatherwise.network

import com.example.weatherwise.model.entities.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

const val APP_ID = "e458d81824ca8cd85c01327409e255e5"

interface WeatherService {


    @GET("onecall")
    suspend fun getWeather(
        @Query("lat") lat : String,
        @Query("lon") lon : String,
        @Query("exclude") exclude : String = "minutely",
        @Query("lang") lang : String,
        @Query("units") units : String,
        @Query("appid") appId : String = APP_ID,
    ) : WeatherResponse
}