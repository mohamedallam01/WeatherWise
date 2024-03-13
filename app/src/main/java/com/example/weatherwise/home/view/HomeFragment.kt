package com.example.weatherwise.home.view

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherwise.R
import com.example.weatherwise.REQUEST_LOCATION_CODE
import com.example.weatherwise.home.viewmodel.HomeViewModel
import com.example.weatherwise.home.viewmodel.HomeViewModelFactory
import com.example.weatherwise.model.WeatherRepoImpl
import com.example.weatherwise.model.WeatherResponse
import com.example.weatherwise.network.ApiState
import com.example.weatherwise.network.WeatherRemoteDataSourceImpl
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private val TAG = "HomeFragment"

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeViewModelFactory: HomeViewModelFactory
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var geocoder : MutableList<Address>
    private lateinit var progressBar : ProgressBar
    private lateinit var tvAddress :TextView
    private lateinit var tvTempDegree :TextView
    private lateinit var tvMain :TextView
    private lateinit var homeHourlyAdapter: HomeHourlyAdapter
    private lateinit var rvHourly : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        progressBar = view.findViewById(R.id.progress_Bar)
        tvAddress = view.findViewById(R.id.tv_address)
        tvTempDegree = view.findViewById(R.id.tv_temp_degree)
        tvMain = view.findViewById(R.id.tv_main)
        rvHourly = view.findViewById(R.id.rv_hourly)

        homeHourlyAdapter = HomeHourlyAdapter(requireContext())
        rvHourly.adapter = homeHourlyAdapter
        rvHourly.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)

        homeViewModelFactory = HomeViewModelFactory(
            WeatherRepoImpl.getInstance(
                WeatherRemoteDataSourceImpl.getInstance()

            )
        )

        val activity = requireActivity()

        Log.d(TAG, "The Activity is: ${activity.title} ")



        homeViewModel = ViewModelProvider(this,homeViewModelFactory).get(HomeViewModel::class.java)

        getFreshLocation()
        lifecycleScope.launch {
            homeViewModel.currentWeather.collectLatest {
                    result ->

                when(result){
                    is ApiState.Loading ->{
                        progressBar.visibility = View.VISIBLE
                    }
                    is ApiState.Success ->{
                        progressBar.visibility = View.GONE
                        Log.d(TAG, "Success Result: ${result.data.hourly} ")
                        setHomeData(result.data)
                        homeHourlyAdapter.submitList(result.data.hourly)
                    }
                    is ApiState.Failure -> {
                        progressBar.visibility = View.GONE
                        Log.d(TAG, "Exception is: ${result.msg}")
                        Toast.makeText(requireActivity(),result.msg.toString(), Toast.LENGTH_SHORT).show()
                    }


                }
            }


        }



    }

    private fun setHomeData(weatherResponse: WeatherResponse){
        val address = weatherResponse.timezone
        val tempDegree = weatherResponse.current.temp
        val main = weatherResponse.current.weather[0].main


        tvAddress.text = address
        tvTempDegree.text = "$tempDegree °C"
        tvMain.text = main


    }






    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_LOCATION_CODE) {
            if (grantResults.size > 1 && grantResults.get(0) == PackageManager.PERMISSION_GRANTED) {
                getFreshLocation()
            }

        }
    }


    @SuppressLint("MissingPermission")
    fun getFreshLocation() {

        Log.d(TAG, "getFreshLocation: ")
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationProviderClient.requestLocationUpdates(
            com.google.android.gms.location.LocationRequest.Builder(0).apply {
                setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            }.build(),
            object : LocationCallback() {

                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)

                    val location = locationResult.lastLocation

                    Log.d(TAG, "onLocationResult: ")

                    val longitude = location?.longitude.toString()
                    val latitude = location?.latitude.toString()

                    homeViewModel.setCurrentLocation(latitude, longitude, "en", "metric")
                    //val response = homeViewModel.getCurrentWeather(latitude, longitude, "en", "metric")



//                    tvLongitude.text = longitude.toString()
//                    tvLatitude.text = latitude.toString()

//                    geocoder =
//                        Geocoder(this@MainActivity).getFromLocation(latitude!!,longitude!!, 1)!!

                    //  Log.d(TAG, "geocoder: $geocoder")

//                   l

                    //tvAddress.text = "Country: $countryName \n Address: $addressLine \n postalCode: $postalCode \n admin: $admin"


                    fusedLocationProviderClient.removeLocationUpdates(this)

                }
            },
            Looper.myLooper()

        )
    }

}