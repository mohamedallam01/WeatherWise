package com.example.weatherwise.favorite.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherwise.R
import com.example.weatherwise.model.DailyForecast
import com.example.weatherwise.model.FavoriteWeather
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FavoriteAdapter (private val context: Context) :
    ListAdapter<FavoriteWeather, FavoriteViewHolder>(FavoriteDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.favorite_item, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val favoriteWeather: FavoriteWeather = getItem(position)

        holder.favoriteAddress.text = favoriteWeather.timezone



    }

}


class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val favoriteAddress: TextView = itemView.findViewById(R.id.tv_favorite_address)


}

class FavoriteDiffUtil : DiffUtil.ItemCallback<FavoriteWeather>() {
    override fun areItemsTheSame(oldItem: FavoriteWeather, newItem: FavoriteWeather): Boolean {
        return oldItem.fav_id == newItem.fav_id
    }

    override fun areContentsTheSame(oldItem: FavoriteWeather, newItem: FavoriteWeather): Boolean {
        return oldItem == newItem
    }
}