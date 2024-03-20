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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class HomeViewModel(private val _repo: WeatherRepo) : ViewModel() {

    private val TAG = "HomeViewModel"

    private val _currentWeather: MutableLiveData<WeatherResponse> = MutableLiveData()
    val currentWeather: LiveData<WeatherResponse> = _currentWeather

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
             _repo.getWeatherResponse().collect{
                _currentWeather.postValue(it)
                 Log.d(TAG, "getCurrentWeather: ${currentWeather.value}")
            }



        }


    }


}