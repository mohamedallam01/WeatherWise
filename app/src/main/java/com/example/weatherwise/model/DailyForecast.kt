package com.example.weatherwise.model


data class DailyForecast(
    var dt: Long = 0,
    var sunrise: Long = 0,
    var sunset: Long = 0,
    var moonrise: Long = 0,
    var moonset: Long = 0,
    var moon_phase: Double = 0.0,
    var summary: String = "",
    var temp: Temperature = Temperature(),
    var feels_like: FeelsLike,
    var pressure: Int = 0,
    var humidity: Int = 0,
    var dew_point: Double = 0.0,
    var wind_speed: Double = 0.0,
    var wind_deg: Int = 0,
    var wind_gust: Double = 0.0,
    var weather: List<WeatherDetails> = listOf(),
    var clouds: Int = 0,
    var pop: Double = 0.0,
    var rain: Double? = 0.0,
    var uvi: Double = 0.0
)

data class Temperature(
    var day: Double = 0.0,
    var min: Double = 0.0,
    var max: Double = 0.0,
    var night: Double = 0.0,
    var eve: Double = 0.0,
    var morn: Double = 0.0
)

data class FeelsLike(
    var day: Double = 0.0,
    var night: Double = 0.0,
    var eve: Double = 0.0,
    var morn: Double =0.0
)



