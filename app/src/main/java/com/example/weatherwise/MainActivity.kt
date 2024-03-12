package com.example.weatherwise


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.weatherwise.alert.AlertFragment
import com.example.weatherwise.favorite.FavoriteFragment
import com.example.weatherwise.home.HomeFragment
import com.example.weatherwise.settings.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private lateinit var bottomNavBar: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavBar = findViewById(R.id.bottom_nav_view)

        loadFragment(HomeFragment())

        val navController = Navigation.findNavController(this,R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(bottomNavBar,navController)
        
        bottomNavBar.setOnItemSelectedListener { it ->

            when (it.itemId) {
                R.id.home -> {
                    loadFragment(HomeFragment())
                    true
                }

                R.id.alert -> {
                    loadFragment(AlertFragment())
                    true
                }

                R.id.favorite -> {
                    loadFragment(FavoriteFragment())
                    true
                }

                else -> {
                    loadFragment(SettingsFragment())
                    true
                }


            }

        }


    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, fragment)
        transaction.commit()

    }




}


//try {
//    lifecycleScope.launch(Dispatchers.IO) {
//        val response =
//            RetrofitHelper.weatherService.getWeather(lat = "33.44", lon = "-94.04", lang = "en")
//
//        if (response.isSuccessful) {
//            Log.d(TAG, "onCreate: ${response.body()}")
//        } else {
//            Log.d(TAG, "onCreate: ${response.message()}")
//        }
//
//    }
//}
//
//catch (e:Exception){
//    Log.d(TAG, "onCreate: ${e.message}")
//}