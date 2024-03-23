package com.example.weatherwise

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedLocationViewModel : ViewModel(){

    private val _latitude = MutableLiveData<Double>()
    val latitude: LiveData<Double> = _latitude

    private val _longitude = MutableLiveData<Double>()
    val longitude: LiveData<Double> = _longitude

    fun updateLocation(latitude: Double, longitude: Double) {
        _latitude.value = latitude
        _longitude.value = longitude
    }
}