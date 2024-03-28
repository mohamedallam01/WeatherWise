package com.example.weatherwise.data.source

import androidx.lifecycle.MutableLiveData
import com.example.weatherwise.model.Alert
import com.example.weatherwise.model.FavoriteWeather
import com.example.weatherwise.model.WeatherRepo
import com.example.weatherwise.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

class FakeWeatherRepository : WeatherRepo {

    private var localDataSource: FakeLocalDataSource = FakeLocalDataSource()
    private lateinit var remoteDataSource: FakeRemoteDataSource

    private val observableWeathers = MutableLiveData<Result<List<WeatherResponse>>>()
    override fun getWeatherResponse(): Flow<WeatherResponse> {
        return localDataSource.getWeatherResponse()
    }

    override fun getCurrentWeatherFromRemote(
        lat: String,
        lon: String,
        language: String,
        units: String
    ): Flow<WeatherResponse> {
        TODO("Not yet implemented")
    }

    override fun getAllAlerts(): Flow<List<Alert>> {
        TODO("Not yet implemented")
    }

    override fun getAllFavorites(): Flow<List<FavoriteWeather>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertAlert(alert: Alert?) {
        TODO("Not yet implemented")
    }

    override suspend fun insertFavorite(favoriteWeather: FavoriteWeather) {
        localDataSource.insertFavorite(favoriteWeather)
    }

    override suspend fun deleteAlert(alert: Alert) {
        TODO("Not yet implemented")
    }

    override fun getFavoriteById(favoriteId: Int): Flow<FavoriteWeather> {
        TODO("Not yet implemented")
    }
}