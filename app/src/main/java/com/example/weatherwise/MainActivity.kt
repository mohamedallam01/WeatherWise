package com.example.weatherwise


import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.BUTTON_POSITIVE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide.init
import com.example.weatherwise.dp.WeatherLocalDataSourceImpl
import com.example.weatherwise.home.view.HomeFragment
import com.example.weatherwise.home.viewmodel.HomeViewModel
import com.example.weatherwise.home.viewmodel.HomeViewModelFactory
import com.example.weatherwise.model.WeatherRepoImpl
import com.example.weatherwise.network.WeatherRemoteDataSourceImpl
import com.example.weatherwise.util.ChecksManager
import com.example.weatherwise.util.ChecksManager.enableLocationService
import com.example.weatherwise.util.INITIAL_CHOICE
import com.example.weatherwise.util.INITIAL_PREFS
import com.example.weatherwise.util.InitialSetupDialog
import com.google.android.material.bottomnavigation.BottomNavigationView

const val REQUEST_LOCATION_CODE = 2005

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"


    private lateinit var bottomNavBar: BottomNavigationView
    private lateinit var navController: NavController
    private lateinit var fragment: FragmentContainerView
    lateinit var initialSetupDialog: InitialSetupDialog
    lateinit var progressBar: ProgressBar


    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeViewModelFactory: HomeViewModelFactory
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        fragment = findViewById(R.id.nav_host_fragment)
        //progressBar = findViewById(R.id.progress_Bar)

        Log.d(TAG, "onCreate: ")


    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")

        if (!::initialSetupDialog.isInitialized) {
            //progressBar.visibility = View.GONE
            initialSetupDialog = InitialSetupDialog()
            initialSetupDialog.setPositiveButton(DialogInterface.OnClickListener { dialogInterface, _ ->
                if (ChecksManager.isLocationIsEnabled(this)) {
                    initMainActivity()
                } else {
                    enableLocationService(this)
                }
            })

            if(!isInitialSetupDone()){
                initialSetupDialog.show(supportFragmentManager, "InitialSetupDialog")
            }

        }



    }
    private fun initMainActivity() {


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
                    R.id.settings -> navController.navigate(
                        R.id.preferences_graph,
                        null,
                        navOptions
                    )

                }
                true
            }


    }

    private fun isInitialSetupDone(): Boolean {
        val sharedPreferences = getSharedPreferences(INITIAL_PREFS, Context.MODE_PRIVATE)
        return sharedPreferences.contains(INITIAL_CHOICE)
    }


}


//        homeViewModelFactory = HomeViewModelFactory(
//            WeatherRepoImpl.getInstance(
//                WeatherRemoteDataSourceImpl.getInstance(),
//                WeatherLocalDataSourceImpl(this)
//
//            )
//        )
//
//        homeViewModel = ViewModelProvider(this, homeViewModelFactory).get(HomeViewModel::class.java)
//
//        val response = homeViewModel.currentWeather
//        Log.d(TAG, "response: ${response.value}")


//    private fun checkPermissions(): Boolean {
//
//        return checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED||
//                checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//
//    }


//    private fun isLocationEnabled(): Boolean {
//
//        val locationManager: LocationManager =
//            getSystemService(Context.LOCATION_SERVICE) as LocationManager
//
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
//            LocationManager.NETWORK_PROVIDER
//        )
//
//    }

//    private fun enableLocationService() {
//        Toast.makeText(this, "Turn On Location", Toast.LENGTH_LONG).show()
//        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//        startActivity(intent)
//    }


//    private fun initUi() {
//        checkPermissions()
//        isLocationEnabled()
//        enableLocationService()
//    }
