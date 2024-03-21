package com.example.weatherwise.network

import android.util.Log
import com.example.weatherwise.model.WeatherResponse
import retrofit2.Response

class WeatherRemoteDataSourceImpl : WeatherRemoteDataSource {


    private val TAG = "WeatherRemoteDataSourceImpl"

    private val weatherService :WeatherService  by lazy {
        RetrofitHelper.weatherService
    }

    companion object{
        private var instance :WeatherRemoteDataSourceImpl? = null
        fun getInstance() : WeatherRemoteDataSourceImpl{
            return instance ?: synchronized(this){
                val temp = WeatherRemoteDataSourceImpl()
                instance = temp
                temp
            }
        }
    }
    override suspend fun getCurrentWeather(
        lat: String,
        lon: String,
        language: String,
        units: String
    ): WeatherResponse {
        val response = weatherService.getWeather(lat = lat,lon = lon, lang = language,units = units)
        Log.d(TAG, "getCurrentWeatherFromRemote: $response ")
        return response
    }


}