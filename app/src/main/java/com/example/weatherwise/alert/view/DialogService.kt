package com.example.weatherwise.alert.view

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager
import com.example.weatherwise.MainActivity
import com.example.weatherwise.R
import com.example.weatherwise.alert.CHANNEL_ID
import com.example.weatherwise.alert.NOTIFICATION_ID
import com.example.weatherwise.dp.WeatherLocalDataSourceImpl
import com.example.weatherwise.model.repo.WeatherRepo
import com.example.weatherwise.model.repo.WeatherRepoImpl
import com.example.weatherwise.network.WeatherRemoteDataSourceImpl
import com.example.weatherwise.util.LANG_KEY
import com.example.weatherwise.util.LATITUDE
import com.example.weatherwise.util.LOCATION
import com.example.weatherwise.util.LONGITUDE
import com.example.weatherwise.util.TEMP_UNIT_KEY
import com.example.weatherwise.home.view.LATITUDE
import com.example.weatherwise.home.view.LOCATION
import com.example.weatherwise.home.view.LONGITUDE
import com.example.weatherwise.model.repo.WeatherRepo
import com.example.weatherwise.model.repo.WeatherRepoImpl
import com.example.weatherwise.network.WeatherRemoteDataSourceImpl
import com.example.weatherwise.preferences.LANG_KEY
import com.example.weatherwise.preferences.TEMP_UNIT_KEY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DialogService : Service() {

    private val TAG = "DialogService"

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: ConstraintLayout
    private var desc: String? = ""
    private lateinit var _repo: WeatherRepo
    private lateinit var tvDesc: TextView
    private var alertType: String? = ""
    private lateinit var locationSharedPreferences: SharedPreferences
    private lateinit var prefsSharedPreferences: SharedPreferences
    private var latitudeFromPrefs: String? = null
    private var longitudeFromPrefs: String? = null
    private var tempUnitFromPrefs: String? = ""
    private var languageFromPrefs: String = ""

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        alertType = intent?.getStringExtra(ALERT_TYPE)
        Log.d(TAG, "alert type: $alertType ")

        if (alertType == "Alarm") {

            showAlarmDialog()

        } else if (alertType == "Notification") {


            showNotification(this)
        }


        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()

        _repo = WeatherRepoImpl.getInstance(
            WeatherRemoteDataSourceImpl(),
            WeatherLocalDataSourceImpl(this)
        )

        locationSharedPreferences =
            this.getSharedPreferences(LOCATION, Context.MODE_PRIVATE)
        prefsSharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(this)


        latitudeFromPrefs =
            locationSharedPreferences.getString(LATITUDE, null)
        longitudeFromPrefs =
            locationSharedPreferences.getString(LONGITUDE, null)

        tempUnitFromPrefs =
            prefsSharedPreferences.getString(TEMP_UNIT_KEY, "metric")
        languageFromPrefs =
            prefsSharedPreferences.getString(LANG_KEY, "en").toString()


    }

    override fun onDestroy() {
        super.onDestroy()
        if (floatingView != null) {
            windowManager.removeView(floatingView)

        }
    }

    private fun getScreenWidth(): Int {

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        return width
    }

    private fun getScreenHeight(): Int {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.widthPixels
        return height
    }

    private fun showNotification(context: Context) {

        CoroutineScope(Dispatchers.Main).launch {
            _repo.getCurrentWeatherFromRemote(
                latitudeFromPrefs.toString(),
                longitudeFromPrefs.toString(),
                languageFromPrefs,
                tempUnitFromPrefs.toString()
            )
                .collectLatest { result ->
                    val goodWeather = "Good Weather, Enjoy"
                    desc =
                        if (result.alerts.isNullOrEmpty() || result.alerts?.get(0)?.description.isNullOrEmpty()) {
                            goodWeather
                        } else {
                            result.alerts?.get(0)?.description ?: goodWeather
                        }
                    Log.d(TAG, "description from notification: $desc ")
                }

            withContext(Dispatchers.Main) {

                val bigTextStyle = NotificationCompat.BigTextStyle().bigText(desc)
                val appIntent = Intent(context, MainActivity::class.java)
                val pendingIntent =
                    PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_IMMUTABLE)
                val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Alert")
                    .setContentText(desc)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setStyle(bigTextStyle)
                    .setContentIntent(pendingIntent)
                    .build()

                val manager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.notify(NOTIFICATION_ID, notification)

            }
        }


    }

    private fun showAlarmDialog() {

        floatingView =
            LayoutInflater.from(this)
                .inflate(R.layout.floating_dialog, null) as ConstraintLayout
        tvDesc = floatingView.findViewById<TextView>(R.id.textView)
        _repo = WeatherRepoImpl.getInstance(
            WeatherRemoteDataSourceImpl.getInstance(),
            WeatherLocalDataSourceImpl(this)
        )


        CoroutineScope(Dispatchers.Main).launch {
            _repo.getCurrentWeatherFromRemote(
                latitudeFromPrefs.toString(),
                longitudeFromPrefs.toString(),
                languageFromPrefs,
                tempUnitFromPrefs.toString()
            )
                .collectLatest { result ->
                    val goodWeather = "Good Weather, Enjoy"
                    desc =
                        if (result.alerts.isNullOrEmpty() || result.alerts?.get(0)?.description.isNullOrEmpty()) {
                            goodWeather
                        } else {
                            result.alerts?.get(0)?.description ?: goodWeather
                        }
                    tvDesc.text = desc
                    Log.d(TAG, "description: $desc ")
                }
        }


        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.addView(floatingView, params)


        floatingView.viewTreeObserver.addOnGlobalLayoutListener {
            floatingView.viewTreeObserver.removeOnGlobalLayoutListener {}


            val xPos = (getScreenWidth() - floatingView.width) / 2
            val yPos = (getScreenHeight() - floatingView.height) / 2

            params.x = xPos
            params.y = yPos
        }

        floatingView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                stopSelf()
                return@setOnTouchListener true
            }
            false
        }

    }
}
