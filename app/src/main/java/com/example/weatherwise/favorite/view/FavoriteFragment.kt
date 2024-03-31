package com.example.weatherwise.favorite.view

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherwise.databinding.FragmentFavoriteBinding
import com.example.weatherwise.dp.WeatherLocalDataSourceImpl
import com.example.weatherwise.favorite.viewmodel.FavoriteViewModel
import com.example.weatherwise.favorite.viewmodel.FavoriteViewModelFactory
import com.example.weatherwise.model.entities.FavoriteWeather
import com.example.weatherwise.model.repo.WeatherRepoImpl
import com.example.weatherwise.network.WeatherRemoteDataSourceImpl
import kotlinx.coroutines.launch


const val FAVORITE_FRAGMENT = "favorite_fragment"

class FavoriteFragment : Fragment(), OnFavClickListener {

    private val TAG = "FavoriteFragment"

    private lateinit var favoriteAdapter: FavoriteAdapter
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var favoriteViewModelFactory: FavoriteViewModelFactory
    private lateinit var binding: FragmentFavoriteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.progressBarFavorite.visibility = View.GONE
        binding.fabAddFav.setOnClickListener {
            val action =
                FavoriteFragmentDirections.actionFavoriteFragmentToMapFragment2(FAVORITE_FRAGMENT)
            findNavController().navigate(action)
        }

        favoriteAdapter = FavoriteAdapter(requireContext(), this)
        binding.rvFavorites.adapter = favoriteAdapter
        binding.rvFavorites.layoutManager =
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

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete Item")
        builder.setMessage("Are you sure you want to delete this item?")
        builder.setPositiveButton("OK") { dialogInterface: DialogInterface, _: Int ->
            lifecycleScope.launch {
                favoriteViewModel.deleteFavorite(favoriteWeather)

            }
            Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show()
            dialogInterface.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        }
        val dialog = builder.create()
        dialog.show()

    }
}



