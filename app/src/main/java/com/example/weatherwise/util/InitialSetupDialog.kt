package com.example.weatherwise.util

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.BUTTON_POSITIVE
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.weatherwise.R


const val INITIAL_PREFS = "InitialPreferences"
const val INITIAL_CHOICE = "initialChoice"
class InitialSetupDialog : DialogFragment() {

    private val TAG = "InitialSetupDialog"

    private var positiveButtonListener: DialogInterface.OnClickListener? = null
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireActivity().getSharedPreferences(INITIAL_PREFS, Context.MODE_PRIVATE)


    }

    fun setPositiveButton(listener: DialogInterface.OnClickListener) {
        positiveButtonListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireActivity())
            .setTitle("Initial Setup")
            .setPositiveButton("OK") { dialog, which ->
                positiveButtonListener?.onClick(dialog, which)
            }
            .setSingleChoiceItems(
                arrayOf("GPS", "Location"), 0
            ) { dialog, which ->
                val selected = if(which == 0) "GPS" else "Location"
                sharedPreferences.edit().putString(INITIAL_CHOICE, selected).commit()
                Log.d(TAG, "Selected: $selected ")
            }
            .create()




    }
}
