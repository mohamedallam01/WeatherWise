package com.example.weatherwise.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {

    private const val baseUrl = "https://api.openweathermap.org/data/3.0/"


    private val retrofit: Retrofit by lazy {

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }

    val weatherService: WeatherService by lazy {
        retrofit.create(WeatherService::class.java)
    }
}