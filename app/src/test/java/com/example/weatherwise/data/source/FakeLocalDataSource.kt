package com.example.weatherwise.data.source

import com.example.weatherwise.dp.WeatherLocalDataSource
import com.example.weatherwise.model.Alert
import com.example.weatherwise.model.FavoriteWeather
import com.example.weatherwise.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocalDataSource() : WeatherLocalDataSource {

    private val localWeathers: Flow<WeatherResponse> = flowOf()
    private val alerts: MutableList<Alert> = mutableListOf()
    private val alertsFlowList: Flow<List<Alert>> = flowOf()
    private val favorites: MutableList<FavoriteWeather> = mutableListOf()
    private val favoritesFlow: Flow<List<FavoriteWeather>> = flowOf()
    private val favoritesFlowById: Flow<FavoriteWeather> = flowOf()
    override fun getWeatherResponse(): Flow<WeatherResponse> {
        return localWeathers
    }

    override suspend fun insertWeatherResponse(weatherResponse: WeatherResponse) {
        TODO("Not yet implemented")
    }

    override suspend fun insertAlert(alert: Alert?) {
        alerts.add(alert!!)
    }

    override suspend fun insertFavorite(favoriteWeather: FavoriteWeather) {
        favorites.add(favoriteWeather)
    }

    override fun getAllAlerts(): Flow<List<Alert>> {
        return alertsFlowList
    }

    override fun getAllFavorites(): Flow<List<FavoriteWeather>> {
        return favoritesFlow
    }

    override fun getFavoriteById(favoriteId: Int): Flow<FavoriteWeather> {
        return favoritesFlowById
    }

    override suspend fun deleteAlert(alert: Alert) {
        TODO("Not yet implemented")
    }
}