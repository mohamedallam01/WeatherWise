package com.example.weatherwise.preferences

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.LocaleManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.util.Log
import android.view.Display.Mode
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.weatherwise.R
import java.util.Locale


const val PREFS = "weather_preferences"
const val TEMP_UNIT_KEY = "temp_unit_key"
const val LOCATION_GPS_KEY = "location_gps_key"
const val WIND_UNIT_KEY = "wind_unit_key"
const val LANG_KEY = "Lang_key"

class PreferencesFragment : PreferenceFragmentCompat() {

    private val TAG = "PreferencesFragment"

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val sharedPreferences = requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)


        findPreference<Preference>("location_gps")?.setOnPreferenceChangeListener { preference, newValue ->
            sharedPreferences.edit().putString(LOCATION_GPS_KEY, newValue.toString()).apply()
            Log.d(TAG, "New Value GPS: $newValue ")
            true
        }

        val savedGPS = sharedPreferences.getString(LOCATION_GPS_KEY, "No saved GPS")
        Log.d(TAG, "Saved GPS: $savedGPS ")

        findPreference<Preference>("temp_unit")?.setOnPreferenceChangeListener { preference, newValue ->
            sharedPreferences.edit().putString(TEMP_UNIT_KEY, newValue.toString()).apply()

            true
        }

        val savedTempUnit = sharedPreferences.getString(TEMP_UNIT_KEY, "No saved Temp unit")
        //Log.d(TAG, "Saved Temp Unit: $savedTempUnit ")


        findPreference<Preference>("wind_unit")?.setOnPreferenceChangeListener { preference, newValue ->
            sharedPreferences.edit().putString(WIND_UNIT_KEY, newValue.toString()).apply()

            true
        }

        val savedWindUnit = sharedPreferences.getString(WIND_UNIT_KEY, "No saved Wind ")
        //Log.d(TAG, "Saved Wind Unit: $savedWindUnit ")


        findPreference<Preference>("language")?.setOnPreferenceChangeListener { preference, newValue ->
            sharedPreferences.edit().putString(LANG_KEY, newValue.toString()).apply()
            updateLocale(newValue.toString())
            true
        }
        val savedLang = sharedPreferences.getString(LANG_KEY, "No saved Language")
        //Log.d(TAG, "Saved Lang: $savedLang ")


        val locationMapPreference: Preference? = findPreference("location_map")
        locationMapPreference?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            findNavController().navigate(R.id.action_preferencesFragment_to_mapFragment)
            true

        }


    }

    private fun updateLocale(language: String) {
        val activity = activity
        if (activity != null && isAdded) {
            val rootView = (activity as? AppCompatActivity)?.findViewById<ViewGroup>(android.R.id.content)?.getChildAt(0)

            rootView?.fadeOutAndIn {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    context?.getSystemService(LocaleManager::class.java)
                        ?.applicationLocales = LocaleList.forLanguageTags(language)
                } else {
                    AppCompatDelegate.setApplicationLocales(
                        LocaleListCompat.forLanguageTags(language)
                    )
                }
                rootView.postDelayed({
                    activity.recreate()
                }, 600)
            }
        }
    }



    private fun View.fadeOutAndIn(onAnimationEnd: () -> Unit) {
        animate().alpha(0f).setDuration(300).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onAnimationEnd()
                animate().alpha(1f).setDuration(300).setListener(null)
            }
        })


    }

    private fun Fragment.applyFadeAnimation() {
        val rootView = requireActivity().findViewById<ViewGroup>(android.R.id.content).getChildAt(0)
        rootView.fadeOutAndIn { requireActivity().recreate() }
    }

    private fun AppCompatActivity.applyFadeAnimation() {
        val rootView = findViewById<ViewGroup>(android.R.id.content).getChildAt(0)
        rootView.fadeOutAndIn { recreate() }
    }






}