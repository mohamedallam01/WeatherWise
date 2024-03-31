package com.example.weatherwise.favorite.view

import com.example.weatherwise.model.entities.FavoriteWeather


interface OnFavClickListener {

    fun moveToDetails(favoriteId : Int)
    fun deleteFavorite(favoriteWeather: FavoriteWeather)
}