package com.example.weatherwise.preferences

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.weatherwise.R
import com.example.weatherwise.util.ChecksManager
import com.example.weatherwise.util.PREFERENCES_FRAGMENT

class PreferencesFragment : PreferenceFragmentCompat() {

    private val TAG = "PreferencesFragment"

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        (activity as? AppCompatActivity)?.findViewById<ViewGroup>(android.R.id.content)
            ?.getChildAt(0)
        findPreference<Preference>("language")?.setOnPreferenceChangeListener { preference, newValue ->
            updateLocale(newValue.toString())
            true
        }

        val locationMapPreference: Preference? = findPreference("location_map")
            locationMapPreference?.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {

                    if (ChecksManager.checkConnection(requireContext())) {
                        val action =
                            PreferencesFragmentDirections.actionPreferencesFragmentToMapFragment(
                                PREFERENCES_FRAGMENT
                            )
                        findNavController().navigate(action)

                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Check Your Internet Connection",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

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