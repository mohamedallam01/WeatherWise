package com.example.weatherwise.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherwise.model.entities.WeatherResponse
import com.example.weatherwise.model.repo.WeatherRepo
import com.example.weatherwise.network.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(private val _repo: WeatherRepo) : ViewModel() {

    private val TAG = "HomeViewModel"

    private val _currentWeather: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Loading)
    val currentWeather = _currentWeather.asStateFlow()

    private val _currentLocationSetting: MutableStateFlow<String?> = MutableStateFlow(null)
    val currentLocationSetting = _currentLocationSetting.asStateFlow()

    private val _currentWeatherFromDatabase: MutableStateFlow<WeatherResponse> = MutableStateFlow(
        WeatherResponse()
    )
    val currentWeatherFromDatabase = _currentWeatherFromDatabase.asStateFlow()


    init {
        getCurrentWeatherFromDataBase()
    }

    fun setCurrentSettings(setting: String) {
        _currentLocationSetting.value = setting
    }

    fun setCurrentLocation(lat: String, lon: String, language: String, units: String) {
        getCurrentWeatherFromRemote(lat, lon, language, units)
    }


    private fun getCurrentWeatherFromRemote(
        lat: String,
        lon: String,
        language: String,
        units: String
    ) {

        viewModelScope.launch(Dispatchers.IO) {
            _repo.getCurrentWeatherFromRemote(lat, lon, language, units)
                .catch { error ->
                    _currentWeather.value = ApiState.Failure(error)
                    Log.d(TAG, "getCurrentWeather: ${currentWeather.value}")
                }
                .collect { data ->
                    _currentWeather.value = ApiState.Success(data)
                }


            Log.d(TAG, "getCurrentWeather: ${currentWeather.value}")

        }


    }

    private fun getCurrentWeatherFromDataBase() {
        viewModelScope.launch(Dispatchers.IO) {
            _repo.getWeatherResponse().collectLatest { weatherResponse ->
                _currentWeatherFromDatabase.value = weatherResponse
                Log.d(TAG, "getCurrentWeather from database: $weatherResponse")
            }
        }
    }


}