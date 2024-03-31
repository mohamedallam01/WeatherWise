package com.example.weatherwise.network

import com.example.weatherwise.model.entities.WeatherResponse

sealed class ApiState {
    class Success(val data : WeatherResponse) : ApiState()
    class Failure(val msg : Throwable) : ApiState()
    data object Loading : ApiState()
}