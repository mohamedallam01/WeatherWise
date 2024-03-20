package com.example.weatherwise.alert.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherwise.model.WeatherRepo
import com.example.weatherwise.network.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AlertViewModel (private val _repo: WeatherRepo) : ViewModel() {

    private val TAG = "AlertViewModel"

    private val _alertWeather: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Loading)
    val alertWeather = _alertWeather.asStateFlow()

    fun setAlertLocation(lat: String, lon: String, language: String, units: String) {
        getAlertWeather(lat, lon, language, units)
    }


    private fun getAlertWeather(
        lat: String,
        lon: String,
        language: String,
        units: String
    ) {

        viewModelScope.launch(Dispatchers.IO) {
            _repo.getCurrentWeatherFromRemote(lat, lon, language, units)
                .catch { error ->
                    _alertWeather.value = ApiState.Failure(error)
                    Log.d(TAG, "getCurrentWeather: ${alertWeather.value}")
                }
                .collect { data ->
                    _alertWeather.value = ApiState.Success(data)
                }


            Log.d(TAG, "getCurrentWeather: ${alertWeather.value}")

        }


    }



}