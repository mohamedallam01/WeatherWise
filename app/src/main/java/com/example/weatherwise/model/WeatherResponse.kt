package com.example.weatherwise.model

data class WeatherResponse(
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezone_offset: Int,
    val current: CurrentWeather,
    val minutely: List<MinutelyForecast>
)


