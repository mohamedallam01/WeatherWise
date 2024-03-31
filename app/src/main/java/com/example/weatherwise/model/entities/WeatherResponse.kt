package com.example.weatherwise.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "weather_table")
data class WeatherResponse(

    @PrimaryKey(autoGenerate = true)
    var id : Int = 0,
    var lat: Double = 0.0,
    var lon: Double = 0.0,
    var timezone: String = "",
    var timezone_offset: Int = 0,
    var current: CurrentWeather = CurrentWeather(),
    //val minutely: List<MinutelyForecast>,
    var daily: List<DailyForecast> = listOf(),
    var hourly: List<HourlyForecast> =  listOf(),
    var alerts : List<Alert>? = null
)


