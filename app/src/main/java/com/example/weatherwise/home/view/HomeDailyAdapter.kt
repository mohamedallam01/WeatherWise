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
import com.example.weatherwise.model.DailyForecast
import com.example.weatherwise.model.HourlyForecast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeDailyAdapter (private val context: Context) :
    ListAdapter<DailyForecast, DailyViewHolder>(DailyDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.daily_item, parent, false)
        return DailyViewHolder(view)
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        val dailyForecast: DailyForecast = getItem(position)

        val day = convertTimestampToday(dailyForecast.dt)
        val icon = convertIconCode(dailyForecast.weather[0].icon)

        holder.dayDaily.text = day
        holder.descDaily.text = dailyForecast.weather[0].description
        val max = dailyForecast.temp.max
        val min = dailyForecast.temp.min
        holder.tempDaily.text = "$max \\ $min"



        Glide.with(context)
            .load(icon)
            .into(holder.iconDaily)
    }

}


fun convertTimestampToday(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("EEE, dd", Locale.getDefault())
    val date = Date(timestamp * 1000)
    return dateFormat.format(date)
}

private fun convertIconCode(code : String) : String{
    return "https://openweathermap.org/img/wn/$code@2x.png"
}

class DailyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val dayDaily: TextView = itemView.findViewById(R.id.tv_day_daily)
    val descDaily: TextView = itemView.findViewById(R.id.tv_desc_daily)
    val iconDaily: ImageView = itemView.findViewById(R.id.iv_icon_daily)
    val tempDaily: TextView = itemView.findViewById(R.id.tv_full_temp_daily)


}

class DailyDiffUtil : DiffUtil.ItemCallback<DailyForecast>() {
    override fun areItemsTheSame(oldItem: DailyForecast, newItem: DailyForecast): Boolean {
        return oldItem.dt == newItem.dt
    }

    override fun areContentsTheSame(oldItem: DailyForecast, newItem: DailyForecast): Boolean {
        return oldItem == newItem
    }
}