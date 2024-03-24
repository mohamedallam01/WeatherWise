package com.example.weatherwise.favorite.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.weatherwise.R

class DetailsFavoriteFragment : Fragment() {

    private val TAG = "DetailsFavoriteFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        val latitude = args.latitude
        val longitude = args.longitude

        Log.d(TAG, "latitude: $latitude, longitude: $longitude ")



    }
}