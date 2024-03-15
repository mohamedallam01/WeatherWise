package com.example.weatherwise.alert

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.weatherwise.R
import java.text.SimpleDateFormat
import java.util.Locale


const val TIME_IN_MILLIS = "time_in_millis"
const val CHANNEL_ID = "weather_id"
const val NOTIFICATION_ID = 1
class NotificationReceiver : BroadcastReceiver() {

    private val TAG = "NotificationReceiver"
    override fun onReceive(context: Context, intent: Intent) {
        val timeInMillis = intent.getLongExtra(TIME_IN_MILLIS, 0)
        val notificationTitle = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(timeInMillis)

        Log.d(TAG, "onReceive: $notificationTitle")
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(notificationTitle)
            .setContentText("Your notification content here")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }


}