package com.example.weatherwise.alert

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.weatherwise.alert.view.ALERT_TYPE
import com.example.weatherwise.alert.view.DialogService


const val ALERT_DESC = "alert_description"
const val CHANNEL_ID = "weather_id"
const val NOTIFICATION_ID = 1

class NotificationReceiver : BroadcastReceiver() {

    private val TAG = "NotificationReceiver"
    private var alertType: String? = ""
    override fun onReceive(context: Context, intent: Intent) {

        alertType = intent.getStringExtra(ALERT_TYPE)

        showDialog(context)

    }




    private fun showDialog(context: Context){
        val serviceIntent = Intent(context, DialogService::class.java).apply {
        }
        serviceIntent.putExtra(ALERT_TYPE,alertType)
        context.startService(serviceIntent)
    }




}