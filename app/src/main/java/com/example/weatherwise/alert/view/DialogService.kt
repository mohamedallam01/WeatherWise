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
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.weatherwise.R
import com.example.weatherwise.alert.ALERT_DESC

class DialogService : Service() {

    private val TAG = "DialogService"

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: ConstraintLayout
    private var desc : String? = ""

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            desc = intent.getStringExtra(ALERT_DESC)

            Log.d(TAG, "Description: $desc ")

            val textView = floatingView.findViewById<TextView>(R.id.textView)
            textView.text = desc
        }
        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()


        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_dialog, null) as ConstraintLayout


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
