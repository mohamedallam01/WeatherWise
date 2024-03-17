package com.example.weatherwise.preferences


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Display.Mode
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.weatherwise.R


const val PREFS = "weather_preferences"
const val TEMP_UNIT_KEY = "temp_unit_key"
const val LOCATION_GPS_KEY = "location_gps_key"
const val WIND_UNIT_KEY = "wind_unit_key"
const val LANG_KEY = "Lang_key"
class PreferencesFragment : PreferenceFragmentCompat() {

    private val TAG = "PreferencesFragment"

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val sharedPreferences = requireContext().getSharedPreferences(PREFS,Context.MODE_PRIVATE)


        findPreference<Preference>("location_gps")?.setOnPreferenceChangeListener{
                preference,newValue-> sharedPreferences.edit().putString(LOCATION_GPS_KEY,newValue.toString()).apply()

            true
        }

        val savedGPS = sharedPreferences.getString(LOCATION_GPS_KEY, "No saved GPS")
        Log.d(TAG, "Saved GPS: $savedGPS ")

        findPreference<Preference>("temp_unit")?.setOnPreferenceChangeListener {
            preference,newValue-> sharedPreferences.edit().putString(TEMP_UNIT_KEY,newValue.toString()).apply()

            true
        }

        val savedTempUnit = sharedPreferences.getString(TEMP_UNIT_KEY,"No saved Temp unit")
        Log.d(TAG, "Saved Temp Unit: $savedTempUnit ")


        findPreference<Preference>("wind_unit")?.setOnPreferenceChangeListener{
                preference,newValue-> sharedPreferences.edit().putString(WIND_UNIT_KEY,newValue.toString()).apply()

            true
        }

        val savedWindUnit = sharedPreferences.getString(WIND_UNIT_KEY, "No saved Wind GPS")
        Log.d(TAG, "Saved Wind Unit: $savedWindUnit ")


        findPreference<Preference>("language")?.setOnPreferenceChangeListener{
                preference,newValue-> sharedPreferences.edit().putString(LANG_KEY,newValue.toString()).apply()

            true
        }
        val savedLang = sharedPreferences.getString(LANG_KEY, "No saved Language")
        Log.d(TAG, "Saved Lang: $savedLang ")


        val locationMapPreference : Preference? = findPreference("location_map")
        locationMapPreference?.onPreferenceClickListener = Preference.OnPreferenceClickListener {


            findNavController().navigate(R.id.action_preferencesFragment_to_mapFragment)
            true

        }



    }
}