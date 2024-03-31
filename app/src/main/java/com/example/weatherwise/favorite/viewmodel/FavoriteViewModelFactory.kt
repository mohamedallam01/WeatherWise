package com.example.weatherwise.favorite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherwise.model.repo.WeatherRepo

class FavoriteViewModelFactory (private val _repo : WeatherRepo) : ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass : Class<T>) : T{
        return if(modelClass.isAssignableFrom(FavoriteViewModel::class.java)){
            FavoriteViewModel(_repo) as T
        }else{

            throw IllegalArgumentException("Favorite View Model Class Not Found")
        }
    }


}