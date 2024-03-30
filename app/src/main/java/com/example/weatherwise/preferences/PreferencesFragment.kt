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
import androidx.preference.SwitchPreference
import com.example.weatherwise.R
import java.util.Locale


const val TEMP_UNIT_KEY = "temp_unit_key"
const val LOCATION_GPS_KEY = "location_gps"
const val WIND_UNIT_KEY = "wind_unit_key"
const val LANG_KEY = "Lang_key"

class PreferencesFragment : PreferenceFragmentCompat() {

    private val TAG = "PreferencesFragment"

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)


        findPreference<Preference>("language")?.setOnPreferenceChangeListener { preference, newValue ->
            updateLocale(newValue.toString())
            true
        }

        val locationMapPreference: Preference? = findPreference("location_map")
        locationMapPreference?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            findNavController().navigate(R.id.action_preferencesFragment_to_mapFragment)
            true
        }


    }

    private fun updateLocale(language: String) {
        val activity = activity
        if (activity != null && isAdded) {
            val rootView =
                (activity as? AppCompatActivity)?.findViewById<ViewGroup>(android.R.id.content)
                    ?.getChildAt(0)

            rootView?.fadeOutAndIn {
                AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.forLanguageTags(language)
                )
            }

        }
    }


    private fun View.fadeOutAndIn(onAnimationEnd: () -> Unit) {
        animate().alpha(0f).setDuration(100).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onAnimationEnd()
                animate().alpha(1f).setDuration(100).setListener(null)
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