package com.example.weatherwise.alert.view

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.weatherwise.R
import com.example.weatherwise.alert.ALERT_DESC

class AlertDialogFragment(private val desc : String?) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Alert Dialog")
                .setMessage(desc)
                .setPositiveButton("OK") { dialog, which ->
                    dialog.dismiss()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}