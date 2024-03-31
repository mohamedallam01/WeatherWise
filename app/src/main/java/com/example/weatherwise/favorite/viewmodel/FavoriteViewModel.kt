package com.example.weatherwise.favorite.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherwise.model.entities.FavoriteWeather
import com.example.weatherwise.model.repo.WeatherRepo
import com.example.weatherwise.network.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import timber.log.Timber

class FavoriteViewModel (private val _repo: WeatherRepo) : ViewModel() {

    private val TAG = "FavoriteViewModel"

    private val _favoriteWeather: MutableStateFlow<List<FavoriteWeather>> = MutableStateFlow(
        emptyList()
    )
    val favoriteWeather = _favoriteWeather.asStateFlow()

    private val _favoriteWeatherById: MutableStateFlow<FavoriteWeather> = MutableStateFlow(
        FavoriteWeather()
    )
    val favoriteWeatherById = _favoriteWeatherById.asStateFlow()


    private val _favWeatherDetails: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Loading)
    val favWeatherDetails: StateFlow<ApiState> = _favWeatherDetails
    fun setFavDetailsLocation(lat: String, lon: String, language: String = "en", units: String = "metric") {

        getFavoriteWeatherDetailsFromRemote(lat,lon,language,units)
    }


     fun getFavoriteWeatherFromDataBase(){

        viewModelScope.launch(Dispatchers.IO) {
            _repo.getAllFavorites().collect {
                _favoriteWeather.value = it
            }
            Timber.tag(TAG).d("get Favorite Weather From database: %s", favoriteWeather.value)
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

    fun deleteFavorite(favoriteWeather: FavoriteWeather){
        viewModelScope.launch(Dispatchers.IO) {
            _repo.deleteFavorite(favoriteWeather)
        }
    }

    fun getFavoriteById(favoriteId : Int){
        viewModelScope.launch {
            _repo.getFavoriteById(favoriteId).collect{
                _favoriteWeatherById.value = it

            }
        }

    }


}