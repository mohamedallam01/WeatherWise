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
import com.example.weatherwise.databinding.FragmentDetailsFavoriteBinding
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
    private lateinit var homeHourlyAdapter: HomeHourlyAdapter
    private lateinit var homeDailyAdapter: HomeDailyAdapter
    private lateinit var binding: FragmentDetailsFavoriteBinding

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
        binding = FragmentDetailsFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = DetailsFavoriteFragmentArgs.fromBundle(
            requireArguments()
        )

        val favoriteId = args.id


        homeHourlyAdapter = HomeHourlyAdapter(requireContext())
        binding.rvHourly.adapter = homeHourlyAdapter
        binding.rvHourly.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        homeDailyAdapter = HomeDailyAdapter(requireContext())
        binding.rvDaily.adapter = homeDailyAdapter
        binding.rvDaily.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        favoriteViewModel.getFavoriteById(favoriteId)

        lifecycleScope.launch {
            var latitude = ""
            var longitude = ""
            favoriteViewModel.favoriteWeatherById.collect { favoriteWeatherDetails ->
                latitude = favoriteWeatherDetails.lat.toString()
                longitude = favoriteWeatherDetails.lon.toString()
                favoriteViewModel.setFavDetailsLocation(latitude, longitude, "en", "metric")

            }
        }


        lifecycleScope.launch {
            favoriteViewModel.favWeatherDetails.collectLatest { result ->

                when (result) {
                    is ApiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is ApiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        Log.d(TAG, "Success Result: ${result.data} ")
                        setHomeData(result.data)
                        homeHourlyAdapter.submitList(result.data.hourly)
                        homeDailyAdapter.submitList(result.data.daily)

                        when (result.data.current.weather[0].main) {
                            "Rain" -> binding.weatherViewFav.setWeatherData(PrecipType.RAIN)
                            "Snow" -> binding.weatherViewFav.setWeatherData(PrecipType.SNOW)
                            "Clear" -> binding.weatherViewFav.setWeatherData(
                          
                                PrecipType.CLEAR
                            )
                        }
                    }

                    is ApiState.Failure -> {
                        binding.progressBar.visibility = View.GONE
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

        binding.cvDetails.visibility = View.VISIBLE
        binding.tvAddress.text = address
        binding.tvTempDegree.text = "$tempDegree °C"
        binding.tvMain.text = main
        binding.tvHumidityDesc.text = humidity.toString()
        binding.tvWindSpeedDesc.text = windSpeed.toString()
        binding.tvPressureDesc.text = pressure.toString()
        binding.tvCloudsDesc.text = clouds.toString()


    }
}