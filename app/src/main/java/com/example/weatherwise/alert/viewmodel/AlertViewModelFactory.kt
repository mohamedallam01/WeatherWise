package com.example.weatherwise.alert.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherwise.home.viewmodel.HomeViewModel
import com.example.weatherwise.model.WeatherRepo

class AlertViewModelFactory (private val _repo : WeatherRepo) : ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass : Class<T>) : T{
        return if(modelClass.isAssignableFrom(AlertViewModel::class.java)){
            AlertViewModel(_repo) as T
        }else{

            throw IllegalArgumentException("AlertViewModel Class Not Found")
        }
    }


}