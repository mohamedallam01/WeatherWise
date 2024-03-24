package com.example.weatherwise.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.example.weatherwise.MainActivity


private const val REQUEST_CODE_DRAW_OVER_OTHER_APPS = 123
object ChecksManager {

    fun checkPermission(context: Context): Boolean {
        var result = false
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            result = true
        }
        return result
    }

    fun isLocationIsEnabled(context: Context): Boolean {
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun enableLocationService(context: Context) {
        Toast.makeText(context, "Turn On Location", Toast.LENGTH_LONG).show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        context.startActivity(intent)

    }

    fun checkConnection(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(
            NetworkCapabilities.TRANSPORT_CELLULAR
        ))
    }

    fun notificationPermission(context: Context): Boolean {
        var result = false
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            result = true
        }
        return result
    }




    fun isDrawOverlayPermissionGranted(activity: Activity): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(activity)
    }

    fun requestDrawOverlayPermission(activity: Activity) {
        if (!isDrawOverlayPermissionGranted(activity)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${activity.packageName}")
            )
            activity.startActivityForResult(intent, REQUEST_CODE_DRAW_OVER_OTHER_APPS)
        }
    }

    fun handleDrawOverlayPermissionResult(requestCode: Int, resultCode: Int,activity: Activity) : Boolean {
        return requestCode == REQUEST_CODE_DRAW_OVER_OTHER_APPS &&
                resultCode == Activity.RESULT_OK &&
                isDrawOverlayPermissionGranted(activity)
    }
}