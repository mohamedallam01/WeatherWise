package com.example.weatherwise.dp

import android.content.Context
import com.example.weatherwise.model.WeatherResponse
import com.example.weatherwise.network.WeatherRemoteDataSourceImpl
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Query


class WeatherLocalDataSourceImpl(context: Context) : WeatherLocalDataSource {

    private val weatherDao: WeatherDao by lazy {
        val database = WeatherDatabase.getInstance(context)
        database.getWeatherDao()
    }

    override fun getWeatherResponse(): Flow<WeatherResponse> {
       return weatherDao.getWeatherResponse()
    }

    override suspend fun insertWeatherResponse(weatherResponse: WeatherResponse) {
        return weatherDao.insertWeatherResponse(weatherResponse)
    }


}