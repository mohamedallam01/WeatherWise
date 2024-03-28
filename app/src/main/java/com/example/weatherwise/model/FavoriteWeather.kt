package com.example.weatherwise.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "favorite_table")
data class FavoriteWeather(
    @PrimaryKey(autoGenerate = true)
    var fav_id : Int = 0,
    var lat: Double = 0.0,
    var lon: Double = 0.0,
    var timezone: String = "",
    //val timezone_offset: Int,
    //val current: CurrentWeather,
    //val minutely: List<MinutelyForecast>,
    //val daily: List<DailyForecast>,
    //val hourly: List<HourlyForecast>,
    //val alerts : List<Alert>?
)
