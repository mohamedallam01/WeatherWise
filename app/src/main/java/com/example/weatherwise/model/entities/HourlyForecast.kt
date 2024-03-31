package com.example.weatherwise.model.entities

data class HourlyForecast(
    var dt: Long = 0,
    var temp: Double = 0.0,
    var feels_like: Double = 0.0,
    var pressure: Int = 0,
    var humidity: Int = 0,
    var dew_point: Double = 0.0,
    var uvi: Double = 0.0,
    var clouds: Int = 0,
    var visibility: Int = 0,
    var wind_speed: Double = 0.0,
    var wind_deg: Int = 0,
    var wind_gust: Double = 0.0,
    var weather: List<WeatherDetails> = listOf(),
    var pop: Double = 0.0
)
