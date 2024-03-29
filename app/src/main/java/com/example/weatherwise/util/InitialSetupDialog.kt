package com.example.weatherwise.util

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.weatherwise.R
import com.example.weatherwise.favorite.view.FAVORITE_FRAGMENT
import com.example.weatherwise.favorite.view.FavoriteFragmentDirections
import com.example.weatherwise.home.view.HomeFragmentDirections
import com.example.weatherwise.home.viewmodel.HomeViewModel
import com.example.weatherwise.map.view.MapFragment
import com.example.weatherwise.preferences.LOCATION_GPS_KEY
import com.example.weatherwise.preferences.PREFS
import com.example.weatherwise.preferences.TEMP_UNIT_KEY


const val INITIAL_PREFS = "InitialPreferences"
const val INITIAL_CHOICE = "initialChoice"
const val HOME_FRAGMENT = "home_fragment"
const val GPS = "GPS"
const val LOCATION = "Location"

class InitialSetupDialog : DialogFragment() {

    private val TAG = "InitialSetupDialog"

    private val homeViewModel: HomeViewModel by activityViewModels()
    private var positiveButtonListener: DialogInterface.OnClickListener? = null
    lateinit var initialPrefs: SharedPreferences
    lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialPrefs = requireActivity().getSharedPreferences(INITIAL_PREFS, Context.MODE_PRIVATE)
        prefs = requireActivity().getSharedPreferences(PREFS, Context.MODE_PRIVATE)


    }

    fun setPositiveButton(listener: DialogInterface.OnClickListener) {
        positiveButtonListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireActivity())
            .setTitle("Initial Setup")
            .setPositiveButton("OK") { dialog, which ->
                positiveButtonListener?.onClick(dialog, which)
                val selected = initialPrefs.getString(INITIAL_CHOICE, GPS) ?: GPS
                if (selected == LOCATION) {
                    val action = HomeFragmentDirections.actionHomeFragmentToMapFragment3(HOME_FRAGMENT)
                    findNavController().navigate(action)
                } else homeViewModel.setCurrentSettings(selected)
                Log.d(TAG, "selected: $selected ")
            }
            .setSingleChoiceItems(
                arrayOf(GPS, "Location"), 0
            ) { dialog, which ->
                val selected = if (which == 0) GPS else LOCATION
                initialPrefs.edit().putString(INITIAL_CHOICE, selected).commit()
                if (which == 0) {
                    prefs.edit().putString(LOCATION_GPS_KEY, true.toString()).apply()
                    val savedGPS = prefs.getString(LOCATION_GPS_KEY, "No saved GPS")
                    Log.d(TAG, "Saved GPS: $savedGPS ")
                } else {
                    prefs.edit().remove(LOCATION_GPS_KEY).apply()
                }
                Log.d(TAG, "Selected: $selected ")
            }
            .create()


    }
}




