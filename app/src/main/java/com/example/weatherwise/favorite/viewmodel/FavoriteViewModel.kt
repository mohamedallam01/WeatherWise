package com.example.weatherwise.favorite.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherwise.model.FavoriteWeather
import com.example.weatherwise.model.WeatherRepo
import com.example.weatherwise.model.WeatherResponse
import com.example.weatherwise.network.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FavoriteViewModel (private val _repo: WeatherRepo) : ViewModel() {

    private val TAG = "FavoriteViewModel"

    private val _favoriteWeather: MutableLiveData<List<FavoriteWeather>> = MutableLiveData()
    val favoriteWeather: LiveData<List<FavoriteWeather>> = _favoriteWeather

    private val _favoriteWeatherById: MutableLiveData<FavoriteWeather> = MutableLiveData()
    val favoriteWeatherById: LiveData<FavoriteWeather> = _favoriteWeatherById


    private val _favWeatherDetails: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Loading)
    val favWeatherDetails: StateFlow<ApiState> = _favWeatherDetails
    fun setFavDetailsLocation(lat: String, lon: String, language: String = "en", units: String = "metric") {

        getFavoriteWeatherDetailsFromRemote(lat,lon,language,units)
    }


     fun getFavoriteWeatherFromDataBase(){

        viewModelScope.launch(Dispatchers.IO) {
            _repo.getAllFavorites().collect {
                _favoriteWeather.postValue(it)
                Log.d(TAG, "get Favorite Weather From database: ${favoriteWeather.value}")
            }
        }

    }



      fun getFavoriteWeatherDetailsFromRemote(
        lat: String,
        lon: String,
        language: String = "en",
        units: String = "metric"
    ) {

        viewModelScope.launch(Dispatchers.IO) {
            _repo.getCurrentWeatherFromRemote(lat, lon, language, units)
                .catch { error ->
                    _favWeatherDetails.value = ApiState.Failure(error)
                    Log.d(TAG, "getCurrentWeather: ${favWeatherDetails.value}")
                }
                .collect { data ->
                    _favWeatherDetails.value = ApiState.Success(data)
                    Log.d(TAG, "Favorite Details: ${data}")
                }




        }


    }

    fun getFavoriteById(favoriteId : Int){
        viewModelScope.launch {
            _repo.getFavoriteById(favoriteId).collect{
                _favoriteWeatherById.postValue(it)

            }
        }

    }


}