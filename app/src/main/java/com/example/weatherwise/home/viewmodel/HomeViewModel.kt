package com.example.weatherwise.home.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherwise.model.WeatherRepo
import com.example.weatherwise.model.WeatherResponse
import com.example.weatherwise.network.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class HomeViewModel(private val _repo: WeatherRepo) : ViewModel() {

    private val TAG = "HomeViewModel"

    private val _currentWeather: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Loading)
    val currentWeather = _currentWeather.asStateFlow()

    private val _currentLocationSetting : MutableStateFlow<String?> = MutableStateFlow(null)
    val currentLocationSetting = _currentLocationSetting.asStateFlow()


    fun setCurrentSettings(setting : String){
        _currentLocationSetting.value = setting
    }

    fun setCurrentLocation(lat: String, lon: String, language: String, units: String) {
        getCurrentWeatherFromDatabase(lat, lon, language, units)
    }


    private fun getCurrentWeatherFromDatabase(
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

}