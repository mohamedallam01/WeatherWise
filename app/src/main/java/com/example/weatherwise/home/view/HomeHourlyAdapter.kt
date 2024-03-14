package com.example.weatherwise.home.view

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
import com.example.weatherwise.model.HourlyForecast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeHourlyAdapter(private val context: Context) :
    ListAdapter<HourlyForecast, HourlyViewHolder>(HourlyDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.hourly_item, parent, false)
        return HourlyViewHolder(view)
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val hourlyForecast: HourlyForecast = getItem(position)

        val time = convertTimestampToDate(hourlyForecast.dt)
        val icon = convertIconCode(hourlyForecast.weather[0].icon)
        holder.timeHourly.text = time
        holder.tempHourly.text = hourlyForecast.temp.toString()



        Glide.with(context)
            .load(icon)
            .into(holder.iconHourly)
    }

}


private fun convertTimestampToDate(timeStamp: Long): String {
    val simpleDateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val date = Date(timeStamp * 1000)
    return simpleDateFormat.format(date)
}

private fun convertIconCode(code : String) : String{
    return "https://openweathermap.org/img/wn/$code@2x.png"
}

class HourlyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val timeHourly: TextView = itemView.findViewById(R.id.tv_time_hourly)
    val iconHourly: ImageView = itemView.findViewById(R.id.iv_icon_hourly)
    val tempHourly: TextView = itemView.findViewById(R.id.tv_temp_hourly)


}

class HourlyDiffUtil : DiffUtil.ItemCallback<HourlyForecast>() {
    override fun areItemsTheSame(oldItem: HourlyForecast, newItem: HourlyForecast): Boolean {
        return oldItem.dt == newItem.dt
    }

    override fun areContentsTheSame(oldItem: HourlyForecast, newItem: HourlyForecast): Boolean {
        return oldItem == newItem
    }
}