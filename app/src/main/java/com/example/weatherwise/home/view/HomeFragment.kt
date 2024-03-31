package com.example.weatherwise.home.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.location.Geocoder
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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherwise.R
import com.example.weatherwise.databinding.FragmentHomeBinding
import com.example.weatherwise.dp.WeatherLocalDataSourceImpl
import com.example.weatherwise.home.viewmodel.HomeViewModel
import com.example.weatherwise.home.viewmodel.HomeViewModelFactory
import com.example.weatherwise.model.entities.WeatherResponse
import com.example.weatherwise.model.repo.WeatherRepoImpl
import com.example.weatherwise.network.ApiState
import com.example.weatherwise.network.WeatherRemoteDataSourceImpl
import com.example.weatherwise.preferences.LANG_KEY
import com.example.weatherwise.preferences.TEMP_UNIT_KEY
import com.example.weatherwise.util.ChecksManager
import com.example.weatherwise.util.GPS
import com.example.weatherwise.util.INITIAL_CHOICE
import com.example.weatherwise.util.INITIAL_PREFS
import com.example.weatherwise.util.getAddress
import com.example.weatherwise.util.round
import com.github.matteobattilana.weather.PrecipType
import com.github.matteobattilana.weather.WeatherView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


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

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var homeHourlyAdapter: HomeHourlyAdapter
    private lateinit var homeDailyAdapter: HomeDailyAdapter
    private lateinit var locationSharedPreferences: SharedPreferences
    private lateinit var prefsSharedPreferences: SharedPreferences
    private lateinit var initialSharedPreferences: SharedPreferences
    private var tempUnitFromPrefs: String? = ""
    private var languageFromPrefs: String = ""
    private var locationInitialPrefs = ""
    private var latitudeFromPrefs: String? = null
    private var longitudeFromPrefs: String? = null
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeViewModelFactory: HomeViewModelFactory
    private lateinit var binding: FragmentHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        Log.d(TAG, "onCreate: ")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "onViewCreated: ")
        binding.cvDetails.visibility = View.GONE
        binding.cvCurrentWeather.visibility = View.GONE



        homeHourlyAdapter = HomeHourlyAdapter(requireContext())
        binding.rvHourly.adapter = homeHourlyAdapter
        binding.rvHourly.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        homeDailyAdapter = HomeDailyAdapter(requireContext())
        binding.rvDaily.adapter = homeDailyAdapter
        binding.rvDaily.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)


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

        homeViewModelFactory = HomeViewModelFactory(
            WeatherRepoImpl.getInstance(
                WeatherRemoteDataSourceImpl.getInstance(),
                WeatherLocalDataSourceImpl(requireContext())
            )
        )

        homeViewModel = ViewModelProvider(this, homeViewModelFactory).get(HomeViewModel::class.java)


        locationSharedPreferences =
            requireContext().getSharedPreferences(LOCATION, Context.MODE_PRIVATE)

        prefsSharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(requireContext())


        initialSharedPreferences =
            requireContext().getSharedPreferences(INITIAL_PREFS, Context.MODE_PRIVATE)

        locationInitialPrefs =
            initialSharedPreferences.getString(INITIAL_CHOICE, "").toString()

        initDataObserver()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                homeViewModel.currentLocationSetting.collectLatest {
                    if (it == GPS)
                        getFreshLocation()
                }
            }
        }
    }


    private fun initDataObserver() {

        if (ChecksManager.checkConnection(requireContext())) {
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    homeViewModel.currentWeather.collectLatest { result ->

                        when (result) {
                            is ApiState.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }

                            is ApiState.Success -> {
                                binding.progressBar.visibility = View.GONE
                                //Log.d(TAG, "Success Result: ${result.data.alerts} ")
                                setHomeData(result.data)
                                homeHourlyAdapter.submitList(result.data.hourly)
                                homeDailyAdapter.submitList(result.data.daily)

                                when (result.data.current.weather[0].main) {
                                    "Rain", "shower rain" -> binding.weatherViewHome.setWeatherData(
                                        PrecipType.RAIN
                                    )

                                    "Snow" -> binding.weatherViewHome.setWeatherData(PrecipType.SNOW)
                                    "Clear" -> binding.weatherViewHome.setWeatherData(
                                        PrecipType.CLEAR
                                    )
                                }
                            }

                            is ApiState.Failure -> {
                                binding.progressBar.visibility = View.GONE
                                Log.d(TAG, "Exception is: ${result.msg}")
                                Toast.makeText(
                                    requireActivity(),
                                    result.msg.toString(),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }

                    }
                }
            }
        } else {
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    homeViewModel.currentWeatherFromDatabase.collectLatest { result ->
                        binding.progressBar.visibility = View.GONE
                        //Log.d(TAG, "Success Result: ${result.data.alerts} ")
                        setHomeData(result)
                        homeHourlyAdapter.submitList(result.hourly)
                        homeDailyAdapter.submitList(result.daily)

                        when (result.current.weather[0].main) {
                            "Rain", "shower rain" -> binding.weatherViewHome.setWeatherData(
                                PrecipType.RAIN
                            )

                            "Snow" -> binding.weatherViewHome.setWeatherData(PrecipType.SNOW)
                            "Clear" -> binding.weatherViewHome.setWeatherData(
                                PrecipType.CLEAR
                            )
                        }

                    }
                }

            }
        }

    }

    override fun onResume() {
        super.onResume()

        getWeatherData()

    }

    private fun getWeatherData() {


        latitudeFromPrefs =
            locationSharedPreferences.getString(LATITUDE, null)
        longitudeFromPrefs =
            locationSharedPreferences.getString(LONGITUDE, null)

        tempUnitFromPrefs =
            prefsSharedPreferences.getString(TEMP_UNIT_KEY, "metric")
        languageFromPrefs =
            prefsSharedPreferences.getString(LANG_KEY, "en").toString()


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
                latitudeFromPrefs!!,
                longitudeFromPrefs!!,
                languageFromPrefs,
                tempUnit
            )
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

        val dateTime = weatherResponse.current.dt
        val tempDegree = weatherResponse.current.temp
        val main = weatherResponse.current.weather[0].main
        val humidity = weatherResponse.current.humidity
        val windSpeed = weatherResponse.current.wind_speed
        val pressure = weatherResponse.current.pressure
        val clouds = weatherResponse.current.clouds

        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            val address =
                geocoder.getAddress(
                    latitudeFromPrefs?.toDouble()?.round(4) ?: 0.0,
                    longitudeFromPrefs?.toDouble()?.round(4) ?: 0.0
                )!!


            val city = address.locality ?: address.extras.getString("sub-admin", "Unknown area")
            Log.d(TAG, "locality $city ")

            binding.cvDetails.visibility = View.VISIBLE
            binding.cvCurrentWeather.visibility = View.VISIBLE
            binding.tvAddress.text = city
            val realDateTime = convertTimestampToDate(dateTime)
            binding.tvDateTime.text = realDateTime
            when (tempUnitFromPrefs) {
                KELVIN -> {
                    binding.tvTempDegree.text = "$tempDegree °K"
                    binding.tvWindSpeedDesc.text = "${windSpeed}  meter/sec"
                }

                FAHRENHEIT -> {
                    binding.tvTempDegree.text = "$tempDegree °F"
                    binding.tvWindSpeedDesc.text = "${windSpeed}  miles/hour"

                }

                else -> {
                    binding.tvTempDegree.text = "$tempDegree °C"
                    binding.tvWindSpeedDesc.text = "${windSpeed}  meter/sec"

                }

            }
            binding.tvMain.text = main
            val humidityUnit = getString(R.string.humidity_unit)
            binding.tvHumidityDesc.text = "$humidity $humidityUnit"
            val pressureUnit = getString(R.string.pressure_unit)
            binding.tvPressureDesc.text = "$pressure $pressureUnit"
            val cloudUnit = getString(R.string.cloud_unit)
            binding.tvCloudsDesc.text = "$clouds $cloudUnit"

        }


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

                    getWeatherData()

                    fusedLocationProviderClient.removeLocationUpdates(this)

                }
            },
            Looper.myLooper()


        )
    }

    private fun convertTimestampToDate(timeStamp: Long): String {
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        val date = Date(timeStamp * 1000)
        return simpleDateFormat.format(date)
    }


}



