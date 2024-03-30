package com.example.weatherwise.home.view

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherwise.R
import com.example.weatherwise.model.DailyForecast
import com.example.weatherwise.preferences.LANG_KEY
import com.example.weatherwise.preferences.TEMP_UNIT_KEY
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeDailyAdapter(private val context: Context) :
    ListAdapter<DailyForecast, DailyViewHolder>(DailyDiffUtil()) {

    private lateinit var prefsSharedPreferences: SharedPreferences
    private var tempUnitFromPrefs: String? = ""
    private var languageFromPrefs: String = ""
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.daily_item, parent, false)
        return DailyViewHolder(view)
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        val dailyForecast: DailyForecast = getItem(position)

        prefsSharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context)
        tempUnitFromPrefs =
            prefsSharedPreferences.getString(TEMP_UNIT_KEY, "No saved Temp unit")
        languageFromPrefs =
            prefsSharedPreferences.getString(LANG_KEY, "No Saved Language").toString()

        val day = convertTimestampToday(dailyForecast.dt)
        val icon = convertIconCode(dailyForecast.weather[0].icon)

        holder.dayDaily.text = day
        holder.descDaily.text = dailyForecast.weather[0].description
        val max = dailyForecast.temp.max
        val min = dailyForecast.temp.min
        val fullTempDaily = "$max \\ $min"

        when (tempUnitFromPrefs) {
            KELVIN -> {
                holder.tempDaily.text = "$fullTempDaily °K"
            }

            FAHRENHEIT -> {
                holder.tempDaily.text = "$fullTempDaily °F"

            }

            else -> {
                holder.tempDaily.text = "$fullTempDaily °C"


            }

        }



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

private fun convertIconCode(code: String): String {
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