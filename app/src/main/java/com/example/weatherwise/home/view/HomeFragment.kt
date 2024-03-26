package com.example.weatherwise.home.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherwise.R
import com.example.weatherwise.dp.WeatherLocalDataSourceImpl
import com.example.weatherwise.home.viewmodel.HomeViewModel
import com.example.weatherwise.home.viewmodel.HomeViewModelFactory
import com.example.weatherwise.map.view.MAP_FRAGMENT
import com.example.weatherwise.model.WeatherRepoImpl
import com.example.weatherwise.model.WeatherResponse
import com.example.weatherwise.network.ApiState
import com.example.weatherwise.network.WeatherRemoteDataSourceImpl
import com.example.weatherwise.preferences.LANG_KEY
import com.example.weatherwise.preferences.LOCATION_GPS_KEY
import com.example.weatherwise.preferences.PREFS
import com.example.weatherwise.preferences.TEMP_UNIT_KEY
import com.example.weatherwise.util.GPS
import com.example.weatherwise.util.INITIAL_CHOICE
import com.example.weatherwise.util.INITIAL_PREFS
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


const val LOCATION = "Location"
const val LATITUDE = "latitude"
const val LONGITUDE = "longitude"
const val KELVIN = "kelvin"
const val CELSIUS = "celsius"
const val FAHRENHEIT = "fahrenheit"
const val METRIC = "metric"
const val STANDARD = "standard"
const val IMPERIAL = "imperial"

class HomeFragment : Fragment() {

    private val TAG = "HomeFragment"

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeViewModelFactory: HomeViewModelFactory
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var progressBar: ProgressBar
    private lateinit var tvAddress: TextView
    private lateinit var tvTempDegree: TextView
    private lateinit var tvMain: TextView
    private lateinit var tvHumidity: TextView
    private lateinit var tvWindSpeed: TextView
    private lateinit var tvPressure: TextView
    private lateinit var tvClouds: TextView
    private lateinit var cvDetails: CardView
    private lateinit var homeHourlyAdapter: HomeHourlyAdapter
    private lateinit var rvHourly: RecyclerView
    private lateinit var rvDaily: RecyclerView
    private lateinit var homeDailyAdapter: HomeDailyAdapter
    private lateinit var locationSharedPreferences: SharedPreferences
    private lateinit var prefsSharedPreferences: SharedPreferences
    private lateinit var initialSharedPreferences: SharedPreferences
    private var tempUnitFromPrefs: String? = ""
    private var languageFromPrefs: String = ""
    private var mapFragmentKey = ""
    private var locationInitialPrefs = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        Log.d(TAG, "onCreate: ")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        Log.d(TAG, "onCreateView: ")
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        Log.d(TAG, "onViewCreated: ")
        progressBar = view.findViewById(R.id.progress_Bar)
        tvAddress = view.findViewById(R.id.tv_address)
        tvTempDegree = view.findViewById(R.id.tv_temp_degree)
        tvMain = view.findViewById(R.id.tv_main)
        rvHourly = view.findViewById(R.id.rv_hourly)
        rvDaily = view.findViewById(R.id.rv_daily)
        tvHumidity = view.findViewById(R.id.tv_humidity_desc)
        tvWindSpeed = view.findViewById(R.id.tv_wind_speed_desc)
        tvPressure = view.findViewById(R.id.tv_pressure_desc)
        tvClouds = view.findViewById(R.id.tv_clouds_desc)
        cvDetails = view.findViewById(R.id.cv_details)
        cvDetails.visibility = View.GONE



        homeHourlyAdapter = HomeHourlyAdapter(requireContext())
        rvHourly.adapter = homeHourlyAdapter
        rvHourly.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        homeDailyAdapter = HomeDailyAdapter(requireContext())
        rvDaily.adapter = homeDailyAdapter
        rvDaily.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        homeViewModelFactory = HomeViewModelFactory(
            WeatherRepoImpl.getInstance(
                WeatherRemoteDataSourceImpl.getInstance(),
                WeatherLocalDataSourceImpl(requireContext())

            )
        )

        homeViewModel = ViewModelProvider(this, homeViewModelFactory).get(HomeViewModel::class.java)

//        val homeArgs = HomeFragmentArgs.fromBundle(requireArguments())
//        mapFragmentKey = homeArgs.mapFragemnt

//        lifecycleScope.launch {
//            homeViewModel.currentWeather.observe(viewLifecycleOwner) { weatherResponse ->
//                weatherResponse?.let {
//                    Log.d(TAG, "currentWeatherFromDatabase: ${it} ")
//                    progressBar.visibility = View.GONE
//                    setHomeData(it)
//                    homeHourlyAdapter.submitList(it.hourly)
//                    homeDailyAdapter.submitList(it.daily)
//                }
//            }


        locationSharedPreferences =
            requireContext().getSharedPreferences(LOCATION, Context.MODE_PRIVATE)

        prefsSharedPreferences =
            requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)

        initialSharedPreferences =
            requireContext().getSharedPreferences(INITIAL_PREFS, Context.MODE_PRIVATE)

        locationInitialPrefs =
            initialSharedPreferences.getString(INITIAL_CHOICE, "No Location").toString()

    }

    override fun onResume() {
        super.onResume()

        Log.d(TAG, "onResume: ")

        if (initialSharedPreferences.getString(INITIAL_CHOICE, "No GPS") == GPS) {

            getFreshLocation()

        } else if (locationInitialPrefs == LOCATION) {
            getLocationFromMap()
        }

        Log.d(
            TAG, "initial choice: $locationInitialPrefs"
        )


        lifecycleScope.launch {
            homeViewModel.currentWeather.collectLatest { result ->

                when (result) {
                    is ApiState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                    }

                    is ApiState.Success -> {
                        progressBar.visibility = View.GONE
                        //Log.d(TAG, "Success Result: ${result.data.alerts} ")
                        setHomeData(result.data)
                        homeHourlyAdapter.submitList(result.data.hourly)
                        homeDailyAdapter.submitList(result.data.daily)
                    }

                    is ApiState.Failure -> {
                        progressBar.visibility = View.GONE
                        Log.d(TAG, "Exception is: ${result.msg}")
                        Toast.makeText(requireActivity(), result.msg.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }


                }
            }


        }

    }

    override fun onStart() {
        super.onStart()

        Log.d(TAG, "onStart: ")
    }

    override fun onPause() {
        super.onPause()

        Log.d(TAG, "onPause: ")
    }

    private fun setHomeData(weatherResponse: WeatherResponse) {
        val address = weatherResponse.timezone
        val tempDegree = weatherResponse.current.temp
        val main = weatherResponse.current.weather[0].main
        val humidity = weatherResponse.current.humidity
        val windSpeed = weatherResponse.current.wind_speed
        val pressure = weatherResponse.current.pressure
        val clouds = weatherResponse.current.clouds


        cvDetails.visibility = View.VISIBLE
        tvAddress.text = address
        when (tempUnitFromPrefs) {
            KELVIN -> {
                tvTempDegree.text = "$tempDegree °K"
                tvWindSpeed.text = "${windSpeed}  meter/sec"
            }

            FAHRENHEIT -> {
                tvTempDegree.text = "$tempDegree °F"
                tvWindSpeed.text = "${windSpeed}  miles/hour"

            }

            else -> {
                tvTempDegree.text = "$tempDegree °C"
                tvWindSpeed.text = "${windSpeed}  meter/sec"

            }

        }
        tvMain.text = main
        tvHumidity.text = humidity.toString()
        tvPressure.text = pressure.toString()
        tvClouds.text = clouds.toString()


    }


    @SuppressLint("MissingPermission")
    fun getFreshLocation() {

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationProviderClient.requestLocationUpdates(
            com.google.android.gms.location.LocationRequest.Builder(0).apply {
                setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            }.build(),
            object : LocationCallback() {

                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)

                    val location = locationResult.lastLocation

                    val longitude = location?.longitude.toString()
                    val latitude = location?.latitude.toString()

                    locationSharedPreferences.edit().putString(LATITUDE, latitude).apply()
                    locationSharedPreferences.edit().putString(LONGITUDE, longitude).apply()


                    val latitudeFromPrefs =
                        locationSharedPreferences.getString(LATITUDE, "No saved Latitude")
                    val longitudeFromPrefs =
                        locationSharedPreferences.getString(LONGITUDE, "No saved Longitude")



                    tempUnitFromPrefs =
                        prefsSharedPreferences.getString(TEMP_UNIT_KEY, "No saved Temp unit")
                    languageFromPrefs =
                        prefsSharedPreferences.getString(LANG_KEY, "No Saved Language").toString()


                    val tempUnit = when (tempUnitFromPrefs) {
                        KELVIN -> STANDARD
                        FAHRENHEIT -> IMPERIAL
                        else -> METRIC

                    }
                    Log.d(TAG, "Temp unit from prefs changed: $tempUnit ")





                    Log.d(
                        TAG,
                        "Latitude fro prefs: $latitudeFromPrefs, Longitude from prefs: $longitudeFromPrefs "
                    )



                    if (latitudeFromPrefs != null && longitudeFromPrefs != null) {
                        homeViewModel.setCurrentLocation(
                            latitudeFromPrefs,
                            longitudeFromPrefs,
                            languageFromPrefs,
                            tempUnit
                        )
                    }
//                    val response = homeViewModel.currentWeather.value
//                    Log.d(TAG, "result: $response ")


//                    geocoder =
//                        Geocoder(requireContext()).getFromLocation(30.070988580730607,31.372045263829136, 1)!!
//
//
//                     Log.d(TAG, "Geocoder: $geocoder")
                    fusedLocationProviderClient.removeLocationUpdates(this)

                }
            },
            Looper.myLooper()


        )
    }

    @SuppressLint("MissingPermission")
    fun getLocationFromMap() {


        val latitudeFromPrefs =
            locationSharedPreferences.getString(LATITUDE, "No saved Latitude")
        val longitudeFromPrefs =
            locationSharedPreferences.getString(LONGITUDE, "No saved Longitude")



        tempUnitFromPrefs =
            prefsSharedPreferences.getString(TEMP_UNIT_KEY, "No saved Temp unit")
        languageFromPrefs =
            prefsSharedPreferences.getString(LANG_KEY, "No Saved Language").toString()


        val tempUnit = when (tempUnitFromPrefs) {
            KELVIN -> STANDARD
            FAHRENHEIT -> IMPERIAL
            else -> METRIC

        }
        Log.d(TAG, "Temp unit from prefs changed: $tempUnit ")

        Log.d(
            TAG,
            "Latitude fro prefs: $latitudeFromPrefs, Longitude from prefs: $longitudeFromPrefs "
        )

        if (latitudeFromPrefs != null && longitudeFromPrefs != null) {
            homeViewModel.setCurrentLocation(
                latitudeFromPrefs,
                longitudeFromPrefs,
                languageFromPrefs,
                tempUnit
            )
        }


    }


}

//    private fun getFavoriteLocation() {
//
//        val latitudeFromPrefs = sharedPreferences.getString(LATITUDE, "")
//        val longitudeFromPrefs = sharedPreferences.getString(LONGITUDE, "")
//
////        Log.d(
////            TAG,
////            "Latitude fro prefs: $latitudeFromPrefs, Longitude from prefs: $longitudeFromPrefs "
////        )
//
//        if (latitudeFromPrefs != null && longitudeFromPrefs != null) {
//            homeViewModel.setCurrentLocation(
//                latitudeFromPrefs,
//                longitudeFromPrefs,
//                "en",
//                "metric"
//            )
//        }
//
//    }



