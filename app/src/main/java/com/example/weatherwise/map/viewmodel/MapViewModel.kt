package com.example.weatherwise.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherwise.model.entities.FavoriteWeather
import com.example.weatherwise.model.repo.WeatherRepo
import com.example.weatherwise.model.entities.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapViewModel (private val _repo: WeatherRepo) : ViewModel() {

    private val TAG = "MapViewModel"

    private val _favoriteWeather: MutableStateFlow<WeatherResponse> = MutableStateFlow(
        WeatherResponse()
    )
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