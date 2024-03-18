package com.example.weatherwise


import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.weatherwise.dp.WeatherLocalDataSourceImpl
import com.example.weatherwise.home.viewmodel.HomeViewModel
import com.example.weatherwise.home.viewmodel.HomeViewModelFactory
import com.example.weatherwise.model.WeatherRepoImpl
import com.example.weatherwise.network.WeatherRemoteDataSourceImpl
import com.google.android.material.bottomnavigation.BottomNavigationView

const val REQUEST_LOCATION_CODE = 2005

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"



    private lateinit var bottomNavBar: BottomNavigationView
    private lateinit var navController: NavController



    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeViewModelFactory: HomeViewModelFactory
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavBar = findViewById(R.id.bottom_nav_view)

        navController = Navigation.findNavController(this, R.id.nav_host_fragment)


        NavigationUI.setupWithNavController(bottomNavBar, navController)

        val navOptions = NavOptions.Builder()
            .setRestoreState(true)
            .setPopUpTo(R.id.home, false, true)
            .build()

        bottomNavBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> navController.navigate(R.id.home_graph, null, navOptions)
                R.id.alert -> navController.navigate(R.id.alert_graph, null, navOptions)
                R.id.favorite -> navController.navigate(R.id.favorite_graph, null, navOptions)
                R.id.settings -> navController.navigate(R.id.preferences_graph, null, navOptions)

            }
            true
        }


        homeViewModelFactory = HomeViewModelFactory(
            WeatherRepoImpl.getInstance(
                WeatherRemoteDataSourceImpl.getInstance(),
                WeatherLocalDataSourceImpl(this)

            )
        )

        homeViewModel = ViewModelProvider(this,homeViewModelFactory).get(HomeViewModel::class.java)
        
        val response = homeViewModel.currentWeather
        Log.d(TAG, "onCreate: ${response.value}")






        initUi()


    }

    override fun onStart() {
        super.onStart()

        if (checkPermissions()) {
            if (isLocationEnabled()) {

            } else {

                enableLocationService()
            }

        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ), REQUEST_LOCATION_CODE
            )
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, fragment)
        transaction.commit()

    }




    private fun checkPermissions(): Boolean {

        return checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED||
                checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    }


    private fun isLocationEnabled(): Boolean {

        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )

    }

    private fun enableLocationService() {
        Toast.makeText(this, "Turn On Location", Toast.LENGTH_LONG).show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }


    
    private fun initUi(){
        checkPermissions()
        isLocationEnabled()
        enableLocationService()


    }




}
