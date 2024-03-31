package com.example.weatherwise.model.entities

data class CurrentWeather(
    var dt: Long = 0,
    var sunrise: Long = 0,
    var sunset: Long = 0,
    var temp: Double = 0.0,
    var feels_like: Double = 0.0,
    var pressure: Int = 0,
    var humidity: Int = 0,
    var dew_point: Double = 0.0,
    var uvi: Double = 0.0,
    var clouds: Int = 0,
    var visibility: Int = 0,
    var wind_speed: Double =0.0,
    var wind_deg: Int =0,
    var weather: List<WeatherDetails> = listOf()
)
