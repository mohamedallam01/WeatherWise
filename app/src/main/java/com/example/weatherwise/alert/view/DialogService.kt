package com.example.weatherwise.alert.view

import android.app.IntentService
import android.content.Intent
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherwise.alert.ALERT_DESC

class DialogService : IntentService("DialogService") {

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {

            val desc = intent.getStringExtra(ALERT_DESC)
            val dialogFragmentIntent = AlertDialogFragment(desc)
            dialogFragmentIntent.show((this as AppCompatActivity).supportFragmentManager,"dialog" )


        }
    }
}