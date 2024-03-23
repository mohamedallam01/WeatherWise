package com.example.weatherwise.favorite.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherwise.model.FavoriteWeather
import com.example.weatherwise.model.WeatherRepo
import com.example.weatherwise.model.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoriteViewModel (private val _repo: WeatherRepo) : ViewModel() {

    private val TAG = "FavoriteViewModel"

    private val _favoriteWeather: MutableLiveData<List<FavoriteWeather>> = MutableLiveData()
    val favoriteWeather: LiveData<List<FavoriteWeather>> = _favoriteWeather
//    fun setAlertLocation(lat: String, lon: String, language: String, units: String) {
//
//        getFavoriteWeatherFromDataBase(lat,lon,language,units)
//    }


     fun getFavoriteWeatherFromDataBase(){

        viewModelScope.launch(Dispatchers.IO) {
            _repo.getAllFavorites().collect {
                _favoriteWeather.postValue(it)
                Log.d(TAG, "get Favorite Weather From database: ${favoriteWeather.value}")
            }
        }

    }


}