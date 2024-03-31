package com.example.weatherwise.favorite.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherwise.R
import com.example.weatherwise.dp.WeatherLocalDataSourceImpl
import com.example.weatherwise.favorite.viewmodel.FavoriteViewModel
import com.example.weatherwise.favorite.viewmodel.FavoriteViewModelFactory
import com.example.weatherwise.home.view.HomeDailyAdapter
import com.example.weatherwise.home.view.HomeHourlyAdapter
import com.example.weatherwise.model.repo.WeatherRepoImpl
import com.example.weatherwise.model.entities.WeatherResponse
import com.example.weatherwise.network.ApiState
import com.example.weatherwise.network.WeatherRemoteDataSourceImpl
import com.github.matteobattilana.weather.PrecipType
import com.github.matteobattilana.weather.WeatherView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DetailsFavoriteFragment : Fragment() {

    private val TAG = "DetailsFavoriteFragment"
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var favoriteViewModelFactory: FavoriteViewModelFactory
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
    private lateinit var weatherViewFavDetails : WeatherView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        favoriteViewModelFactory = FavoriteViewModelFactory(
            WeatherRepoImpl.getInstance(
                WeatherRemoteDataSourceImpl.getInstance(),
                WeatherLocalDataSourceImpl(requireContext())

            )
        )

        favoriteViewModel =
            ViewModelProvider(this, favoriteViewModelFactory).get(FavoriteViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = DetailsFavoriteFragmentArgs.fromBundle(
            requireArguments()
        )

        val favoriteId = args.id

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
        weatherViewFavDetails = view.findViewById(R.id.weather_view_favorite_details)


        homeHourlyAdapter = HomeHourlyAdapter(requireContext())
        rvHourly.adapter = homeHourlyAdapter
        rvHourly.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        homeDailyAdapter = HomeDailyAdapter(requireContext())
        rvDaily.adapter = homeDailyAdapter
        rvDaily.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        favoriteViewModel.getFavoriteById(favoriteId)

        lifecycleScope.launch {
            var latitude = ""
            var longitude = ""
            favoriteViewModel.favoriteWeatherById.collect{favoriteWeatherDetails ->
                latitude = favoriteWeatherDetails.lat.toString()
                longitude = favoriteWeatherDetails.lon.toString()
                favoriteViewModel.setFavDetailsLocation(latitude,longitude,"en","metric")

            }
        }


        lifecycleScope.launch {
            favoriteViewModel.favWeatherDetails.collectLatest { result ->

                when (result) {
                    is ApiState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                    }

                    is ApiState.Success -> {
                        progressBar.visibility = View.GONE
                        Log.d(TAG, "Success Result: ${result.data} ")
                        setHomeData(result.data)
                        homeHourlyAdapter.submitList(result.data.hourly)
                        homeDailyAdapter.submitList(result.data.daily)

                        when (result.data.current.weather[0].main) {
                            "Rain" -> weatherViewFavDetails.setWeatherData(PrecipType.RAIN)
                            "Snow" -> weatherViewFavDetails.setWeatherData(PrecipType.SNOW)
                            "Clear" -> weatherViewFavDetails.setWeatherData(
                                PrecipType.CLEAR
                            )
                        }
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
        tvTempDegree.text = "$tempDegree °C"
        tvMain.text = main
        tvHumidity.text = humidity.toString()
        tvWindSpeed.text = windSpeed.toString()
        tvPressure.text = pressure.toString()
        tvClouds.text = clouds.toString()


    }
}