package com.example.weatherwise.map.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherwise.model.Alert
import com.example.weatherwise.model.FavoriteWeather
import com.example.weatherwise.model.WeatherRepo
import com.example.weatherwise.model.WeatherResponse
import com.example.weatherwise.network.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MapViewModel (private val _repo: WeatherRepo) : ViewModel() {

    private val TAG = "MapViewModel"

    private val _favoriteWeather: MutableStateFlow<WeatherResponse> = MutableStateFlow(WeatherResponse())
     val favoriteWeather = _favoriteWeather.asStateFlow()
//    fun setAlertLocation(lat: String, lon: String, language: String, units: String) {
//
//        getFavoriteWeatherFromDataBase(lat,lon,language,units)
//    }


//    private fun getFavoriteWeatherFromDataBase(
//        lat: String,
//        lon: String,
//        language: String,
//        units: String
//    ) {
//
//        viewModelScope.launch(Dispatchers.IO) {
//            _repo.getFavoriteWeather().collect {
//                _favoriteWeather.postValue(it)
//                Log.d(TAG, "getCurrentWeather From database: ${favoriteWeather.value}")
//            }
//
//        }
//
//
//    }

    fun insertFavoriteWeather(favoriteWeather: FavoriteWeather){
        viewModelScope.launch(Dispatchers.IO) {
            _repo.insertFavorite(favoriteWeather)
        }
    }

}