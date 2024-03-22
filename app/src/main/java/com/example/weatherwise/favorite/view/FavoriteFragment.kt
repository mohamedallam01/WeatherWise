package com.example.weatherwise.favorite.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherwise.R
import com.example.weatherwise.dp.WeatherLocalDataSourceImpl
import com.example.weatherwise.favorite.viewmodel.FavoriteViewModel
import com.example.weatherwise.favorite.viewmodel.FavoriteViewModelFactory
import com.example.weatherwise.home.view.HomeHourlyAdapter
import com.example.weatherwise.home.viewmodel.HomeViewModel
import com.example.weatherwise.home.viewmodel.HomeViewModelFactory
import com.example.weatherwise.model.WeatherRepoImpl
import com.example.weatherwise.network.WeatherRemoteDataSourceImpl
import com.google.android.material.floatingactionbutton.FloatingActionButton


class FavoriteFragment : Fragment() {

    private val TAG = "FavoriteFragment"

    private lateinit var fabAddFav : FloatingActionButton
    private lateinit var favoriteAdapter: FavoriteAdapter
    private lateinit var rvFavorites : RecyclerView
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var favoriteViewModelFactory: FavoriteViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


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

        fabAddFav.setOnClickListener {
            findNavController().navigate(R.id.action_favoriteFragment_to_mapFragment2)
        }

        favoriteAdapter = FavoriteAdapter(requireContext())
        rvFavorites.adapter = favoriteAdapter
        rvFavorites.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        favoriteViewModelFactory = FavoriteViewModelFactory(
            WeatherRepoImpl.getInstance(
                WeatherRemoteDataSourceImpl.getInstance(),
                WeatherLocalDataSourceImpl(requireContext())

            )
        )

        favoriteViewModel = ViewModelProvider(this, favoriteViewModelFactory).get(FavoriteViewModel::class.java)


        favoriteViewModel.getFavoriteWeatherFromDataBase()
        favoriteViewModel.favoriteWeather.observe(viewLifecycleOwner){
            favoriteAdapter.submitList(it)
        }


    }

}