package com.example.weatherwise.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey


@Entity(tableName = "weather_table")
data class WeatherResponse(

    @PrimaryKey(autoGenerate = true)
    val id : Int,
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezone_offset: Int,
    val current: CurrentWeather,
    //val minutely: List<MinutelyForecast>,
    val daily: List<DailyForecast>,
    val hourly: List<HourlyForecast>,
    val alerts : List<Alert>?
)


