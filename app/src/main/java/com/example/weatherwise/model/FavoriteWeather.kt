package com.example.weatherwise.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "favorite_table")
data class FavoriteWeather(
    @PrimaryKey(autoGenerate = true)
    val fav_id : Int = 0,
    val lat: Double,
    val lon: Double,
    val timezone: String,
    //val timezone_offset: Int,
    //val current: CurrentWeather,
    //val minutely: List<MinutelyForecast>,
    //val daily: List<DailyForecast>,
    //val hourly: List<HourlyForecast>,
    //val alerts : List<Alert>?
)
