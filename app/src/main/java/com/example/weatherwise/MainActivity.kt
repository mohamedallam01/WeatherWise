package com.example.weatherwise


import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.weatherwise.dp.WeatherLocalDataSourceImpl
import com.example.weatherwise.home.viewmodel.HomeViewModel
import com.example.weatherwise.home.viewmodel.HomeViewModelFactory
import com.example.weatherwise.util.ChecksManager
import com.example.weatherwise.util.ChecksManager.enableLocationService
import com.example.weatherwise.util.INITIAL_CHOICE
import com.example.weatherwise.util.INITIAL_PREFS
import com.example.weatherwise.util.InitialSetupDialog
import com.google.android.material.bottomnavigation.BottomNavigationView

const val REQUEST_CODE = 2005

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"


    private lateinit var bottomNavBar: BottomNavigationView
    private lateinit var navController: NavController
    private lateinit var fragment: FragmentContainerView
    lateinit var initialSetupDialog: InitialSetupDialog
    // lateinit var progressBar: ProgressBar

    private lateinit var initialSharedPreferences: SharedPreferences


    private var isPermissionGranted = false


    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeViewModelFactory: HomeViewModelFactory
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialSharedPreferences = getSharedPreferences(INITIAL_PREFS, Context.MODE_PRIVATE)
        fragment = findViewById(R.id.nav_host_fragment)

        Log.d(TAG, "onCreate: ")


        initMainActivity()


    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")

        if (!::initialSetupDialog.isInitialized) {
            initialSetupDialog = InitialSetupDialog()
            initialSetupDialog.setPositiveButton(DialogInterface.OnClickListener { dialogInterface, _ ->
                if (ChecksManager.isLocationIsEnabled(this)) {
                    Log.d(TAG, "First initMainActivity: 11111111111 ")

                } else {
                    enableLocationService(this)
                }
            })

            if (ChecksManager.checkPermission(this) && ChecksManager.notificationPermission(this)) {
                if (!isInitialSetupDone()) {
                    initialSetupDialog.show(supportFragmentManager, "InitialSetupDialog")
                }
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.POST_NOTIFICATIONS,
                    ), REQUEST_CODE
                )
            }
        } else if (isPermissionGranted && isInitialSetupDone()) {
            //progressBar.visibility = View.VISIBLE
            Log.d(TAG, "Second initMainActivity: 222222222 ")
            initMainActivity()

        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
    }

    override fun onPause() {
        super.onPause()

        Log.d(TAG, "onPause: ")

    }

    override fun onStop() {
        super.onStop()

        Log.d(TAG, "onStop: ")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            val locationGranted = grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
            val notificationGranted = grantResults.size > 1 &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED

            if (locationGranted && notificationGranted && !isInitialSetupDone()) {
                isPermissionGranted = true
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
        return initialSharedPreferences.contains(INITIAL_CHOICE)
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
