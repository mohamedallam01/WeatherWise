package com.example.weatherwise.model

import android.util.Log
import com.example.weatherwise.dp.WeatherLocalDataSource
import com.example.weatherwise.network.WeatherRemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class WeatherRepoImpl  constructor(
    private val weatherRemoteDataSource: WeatherRemoteDataSource,
    private val weatherLocalDataSource: WeatherLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : WeatherRepo {

    private val TAG = "WeatherRepoImpl"


    companion object {
        private var instance: WeatherRepoImpl? = null
        fun getInstance(
            weatherRemoteDataSource: WeatherRemoteDataSource,
            weatherLocalDataSource: WeatherLocalDataSource

        ): WeatherRepoImpl {

            return instance ?: synchronized(this) {
                val temp = WeatherRepoImpl(
                    weatherRemoteDataSource,
                    weatherLocalDataSource
                )
                instance = temp
                temp
            }


        }
    }

    override fun getWeatherResponse(): Flow<WeatherResponse> {
       // Log.d(TAG, "getWeatherResponse: ${weatherLocalDataSource.getWeatherResponse()}")
        return weatherLocalDataSource.getWeatherResponse()
    }

    override fun getCurrentWeatherFromRemote(
        lat: String,
        lon: String,
        language: String,
        units: String
    ): Flow<WeatherResponse> = flow {


        val weatherResponseFromRemote = weatherRemoteDataSource.getCurrentWeather(
            lat = lat,
            lon = lon,
            language = language,
            units = units
        )

        if (weatherResponseFromRemote != null) {

            try {
                //weatherLocalDataSource.insertWeatherResponse(weatherResponseFromRemote)
                //Log.d(TAG, "getCurrentWeatherFromRemote: $weatherResponseFromRemote")
            } catch (exception: Exception) {

                //Log.d(TAG, "Exception: $exception ")

            }

        } else {
            //Log.d(TAG, "getCurrentWeatherFromRemote: Null")
        }
        emit(weatherResponseFromRemote)

    }.flowOn(Dispatchers.IO)


    override fun getAllAlerts(): Flow<List<Alert>> {
         return weatherLocalDataSource.getAllAlerts()
    }

    override fun getAllFavorites(): Flow<List<FavoriteWeather>> {
       return weatherLocalDataSource.getAllFavorites()
    }

    override suspend fun insertAlert(alert: Alert?) {
        weatherLocalDataSource.insertAlert(alert)
    }

    override suspend fun insertFavorite(favoriteWeather: FavoriteWeather) {
        weatherLocalDataSource.insertFavorite(favoriteWeather)
    }

    override suspend fun deleteAlert(alert: Alert?) {
        weatherLocalDataSource.deleteAlert(alert)
    }

    override fun getFavoriteById(favoriteId: Int): Flow<FavoriteWeather> {
        return weatherLocalDataSource.getFavoriteById(favoriteId)
    }
}