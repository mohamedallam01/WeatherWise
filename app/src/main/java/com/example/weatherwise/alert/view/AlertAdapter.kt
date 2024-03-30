package com.example.weatherwise.alert.view

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
import com.example.weatherwise.favorite.view.OnFavClickListener
import com.example.weatherwise.model.Alert
import com.example.weatherwise.model.DailyForecast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AlertAdapter (private val context: Context,private val listener: OnAlertClickListener) :
    ListAdapter<Alert, AlertViewHolder>(AlertDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.alert_item, parent, false)
        return AlertViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val alert: Alert = getItem(position)

        holder.alertAddress.text = alert.location
        holder.alertDate.text = alert.date
        holder.alertTime.text = alert.time
        holder.ivDeleteAlert.setOnClickListener {
            listener.deleteAlert(alert)
        }


    }

}


class AlertViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val alertAddress: TextView = itemView.findViewById(R.id.tv_alert_address)
    val alertDate: TextView = itemView.findViewById(R.id.tv_alert_date)
    val alertTime: TextView = itemView.findViewById(R.id.tv_alert_time)
    val ivDeleteAlert : ImageView = itemView.findViewById(R.id.iv_delete_alert)


}

class AlertDiffUtil : DiffUtil.ItemCallback<Alert>() {
    override fun areItemsTheSame(oldItem: Alert, newItem: Alert): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Alert, newItem: Alert): Boolean {
        return oldItem == newItem
    }
}