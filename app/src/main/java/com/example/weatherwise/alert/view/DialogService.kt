package com.example.weatherwise.alert.view

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.weatherwise.R
import com.example.weatherwise.alert.ALERT_DESC
import com.example.weatherwise.dp.WeatherLocalDataSource
import com.example.weatherwise.dp.WeatherLocalDataSourceImpl
import com.example.weatherwise.model.Alert
import com.example.weatherwise.model.WeatherRepo
import com.example.weatherwise.model.WeatherRepoImpl
import com.example.weatherwise.network.ApiState
import com.example.weatherwise.network.WeatherRemoteDataSource
import com.example.weatherwise.network.WeatherRemoteDataSourceImpl
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

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()

        floatingView =
            LayoutInflater.from(this).inflate(R.layout.floating_dialog, null) as ConstraintLayout
        tvDesc = floatingView.findViewById<TextView>(R.id.textView)
        _repo = WeatherRepoImpl.getInstance(
            WeatherRemoteDataSourceImpl.getInstance(),
            WeatherLocalDataSourceImpl(this)
        )


        CoroutineScope(Dispatchers.Main).launch {
            _repo.getCurrentWeatherFromRemote("33.44","-94.04","en","metric").collectLatest { result ->
                val goodWeather = "Good Weather, Enjoy"
                desc = if (result.alerts.isNullOrEmpty() || result.alerts?.get(0)?.description.isNullOrEmpty()) {
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
}
