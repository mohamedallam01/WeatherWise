package com.example.weatherwise.favorite.view

import android.app.PendingIntent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherwise.R
import com.example.weatherwise.SharedLocationViewModel
import com.example.weatherwise.dp.WeatherLocalDataSourceImpl
import com.example.weatherwise.favorite.viewmodel.FavoriteViewModel
import com.example.weatherwise.favorite.viewmodel.FavoriteViewModelFactory
import com.example.weatherwise.model.FavoriteWeather
import com.example.weatherwise.model.WeatherRepoImpl
import com.example.weatherwise.network.WeatherRemoteDataSourceImpl
import com.github.matteobattilana.weather.WeatherView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch


const val FAVORITE_FRAGMENT = "favorite_fragment"

class FavoriteFragment : Fragment(), OnFavClickListener {

    private val TAG = "FavoriteFragment"

    private lateinit var fabAddFav: FloatingActionButton
    private lateinit var favoriteAdapter: FavoriteAdapter
    private lateinit var rvFavorites: RecyclerView
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var favoriteViewModelFactory: FavoriteViewModelFactory
    private lateinit var sharedLocationViewModel: SharedLocationViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var progressBarFavorite: ProgressBar
    private var isLocationUpdated: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedLocationViewModel =
            ViewModelProvider(requireActivity()).get(SharedLocationViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fabAddFav = view.findViewById(R.id.fab_add_fav)
        rvFavorites = view.findViewById(R.id.rv_favorites)
        progressBarFavorite = view.findViewById(R.id.progress_Bar_favorite)
        progressBarFavorite.visibility = View.GONE

        fabAddFav.setOnClickListener {
            val action =
                FavoriteFragmentDirections.actionFavoriteFragmentToMapFragment2(FAVORITE_FRAGMENT)
            findNavController().navigate(action)
        }

        favoriteAdapter = FavoriteAdapter(requireContext(), this)
        rvFavorites.adapter = favoriteAdapter
        rvFavorites.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        favoriteViewModelFactory = FavoriteViewModelFactory(
            WeatherRepoImpl.getInstance(
                WeatherRemoteDataSourceImpl.getInstance(),
                WeatherLocalDataSourceImpl(requireContext())

            )
        )

        favoriteViewModel =
            ViewModelProvider(this, favoriteViewModelFactory).get(FavoriteViewModel::class.java)

        favoriteViewModel.getFavoriteWeatherFromDataBase()
        lifecycleScope.launch {
            favoriteViewModel.favoriteWeather.collect {
                favoriteAdapter.submitList(it)
            }
        }

    }

    override fun moveToDetails(favoriteId: Int) {
        Log.d(TAG, "id: $favoriteId ")

        val action =
            FavoriteFragmentDirections.actionFavoriteFragmentToDetailsFavoriteFragment(favoriteId)
        findNavController().navigate(action)
    }

    override fun deleteFavorite(favoriteWeather: FavoriteWeather) {
        lifecycleScope.launch {
            favoriteViewModel.deleteFavorite(favoriteWeather)

        }
    }
}


//if (!isLocationUpdated) {
//    progressBarFavorite.visibility = View.VISIBLE
//    sharedLocationViewModel.latitude.observe(viewLifecycleOwner) { latitude ->
//        sharedLocationViewModel.longitude.observe(viewLifecycleOwner) { longitude ->
//            sharedPreferences.edit().putString(LATITUDE, latitude.toString()).apply()
//            sharedPreferences.edit().putString(LONGITUDE, longitude.toString()).apply()
//            Log.d(TAG, "latitude from shared view model: $latitude ")
//            Log.d(TAG, "longitude from shared view model: $longitude ")
//            isLocationUpdated = true
//            //findNavController().navigate(R.id.action_favoriteFragment_to_homeFragment2)
//        }
//    }
//}


