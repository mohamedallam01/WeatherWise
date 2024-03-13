package com.example.weatherwise.model

import com.example.weatherwise.network.WeatherRemoteDataSource
import com.example.weatherwise.network.WeatherRemoteDataSourceImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class WeatherRepoImpl private constructor(
    private val weatherRemoteDataSource: WeatherRemoteDataSource
) : WeatherRepo {


    companion object {
        private var instance: WeatherRepoImpl? = null
        fun getInstance(
            weatherRemoteDataSource: WeatherRemoteDataSource,

            ): WeatherRepoImpl {

            return instance ?: synchronized(this) {
                val temp = WeatherRepoImpl(
                    weatherRemoteDataSource,
                )
                instance = temp
                temp
            }


        }
    }

    override fun getCurrentWeather(
        lat: String,
        lon: String,
        language: String,
        units: String
    ): Flow<WeatherResponse> = flow {

        emit(
            weatherRemoteDataSource.getCurrentWeather(
                lat = lat,
                lon = lon,
                language = language,
                units = units
            )
        )

    }.flowOn(Dispatchers.IO)
}