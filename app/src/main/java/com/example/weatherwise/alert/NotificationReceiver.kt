package com.example.weatherwise.alert

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.weatherwise.MainActivity
import com.example.weatherwise.R
import java.text.SimpleDateFormat
import java.util.Locale


const val ALERT_DESC = "alert_description"
const val CHANNEL_ID = "weather_id"
const val NOTIFICATION_ID = 1
class NotificationReceiver : BroadcastReceiver() {

    private val TAG = "NotificationReceiver"
    override fun onReceive(context: Context, intent: Intent) {


        val desc = intent.getStringExtra(ALERT_DESC)
        val bigTextStyle = NotificationCompat.BigTextStyle().bigText(desc)
        val appIntent = Intent(context,MainActivity::class.java)
        val pendingIntent =PendingIntent.getActivity(context,0,appIntent,PendingIntent.FLAG_IMMUTABLE)

        Log.d(TAG, "onReceive: $desc")

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Alert")
            .setContentText(desc)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setStyle(bigTextStyle)
            .setContentIntent(pendingIntent)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }


}