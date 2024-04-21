package com.example.weatherwise.map.view

import android.content.Context
import android.content.SharedPreferences
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.weatherwise.R
import com.example.weatherwise.databinding.FragmentMapBinding
import com.example.weatherwise.dp.WeatherLocalDataSourceImpl
import com.example.weatherwise.map.viewmodel.MapViewModel
import com.example.weatherwise.map.viewmodel.MapViewModelFactory
import com.example.weatherwise.model.entities.FavoriteWeather
import com.example.weatherwise.model.repo.WeatherRepoImpl
import com.example.weatherwise.network.WeatherRemoteDataSourceImpl
import com.example.weatherwise.util.FAVORITE_FRAGMENT
import com.example.weatherwise.util.HOME_FRAGMENT
import com.example.weatherwise.util.LATITUDE
import com.example.weatherwise.util.LOCATION
import com.example.weatherwise.util.LONGITUDE
import com.example.weatherwise.util.PREFERENCES_FRAGMENT
import com.example.weatherwise.util.getAddress
import com.example.weatherwise.util.round
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.Locale



class MapFragment : Fragment(), MapEventsReceiver {

    private lateinit var mapViewModel: MapViewModel
    private lateinit var mapViewModelFactory: MapViewModelFactory
    private lateinit var mapPrefs: SharedPreferences
    private var originFragment: String? = ""
    private val TAG = "MapFragment"
    private lateinit var binding: FragmentMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(
            context,
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext())
        )

        mapPrefs = requireContext().getSharedPreferences(LOCATION, Context.MODE_PRIVATE)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        mapViewModelFactory = MapViewModelFactory(
            WeatherRepoImpl.getInstance(
                WeatherRemoteDataSourceImpl.getInstance(),
                WeatherLocalDataSourceImpl(requireContext())

            )
        )

        mapViewModel = ViewModelProvider(this, mapViewModelFactory).get(MapViewModel::class.java)

        binding.mapView.setBuiltInZoomControls(true)
        binding.mapView.mapCenter

        binding.mapView.setMultiTouchControls(true)

        val myLocationProvider = GpsMyLocationProvider(requireContext())
        val myLocationOverlay = MyLocationNewOverlay(myLocationProvider, binding.mapView)
        binding.mapView.overlays.add(myLocationOverlay)
        myLocationOverlay.enableMyLocation()
        myLocationOverlay.enableFollowLocation()

        val mapViewController = binding.mapView.controller
        mapViewController.setZoom(20)


        val mapEventsOverlay = MapEventsOverlay(this)
        binding.mapView.overlays.add(0, mapEventsOverlay)


    }

    override fun singleTapConfirmedHelper(geoPoint: GeoPoint?): Boolean {
        if (geoPoint != null) {

            binding.mapView.overlays.removeAll { it is Marker }

            val marker = Marker(binding.mapView)
            marker.position = geoPoint
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            binding.mapView.overlays.add(marker)

            val mapViewController = binding.mapView.controller
            mapViewController.setCenter(geoPoint)

            val latitude = geoPoint.latitude
            val longitude = geoPoint.longitude


            var fav = FavoriteWeather()
            var city = ""
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                val address =
                    geocoder.getAddress(
                        latitude.round(4),
                        longitude.round(4)
                    )

                withContext(Dispatchers.Main) {
                    city = (address?.locality ?: address?.extras?.getString("sub-admin", "Unknown area")) ?:"inKnown"
                    fav = FavoriteWeather(lat = latitude, lon = longitude, timezone = city)

                }

            }

            Log.d(TAG, "city from geocoder: $city ")

            val mapArgs = MapFragmentArgs.fromBundle(requireArguments())
            originFragment = mapArgs.originFragment
            Log.d(TAG, "origin fragment: $originFragment ")

            binding.btnConfirm.setOnClickListener {
                if (originFragment == HOME_FRAGMENT) {
                    mapPrefs.edit().putString(LATITUDE, latitude.toString()).apply()
                    mapPrefs.edit().putString(LONGITUDE, longitude.toString()).apply()
                    findNavController().navigate(R.id.action_mapFragment3_to_homeFragment)

                } else if (originFragment == FAVORITE_FRAGMENT) {
                    findNavController().navigate(R.id.action_mapFragment2_to_favoriteFragment)
                    mapViewModel.insertFavoriteWeather(fav)
                } else if (originFragment == PREFERENCES_FRAGMENT) {
                    mapPrefs.edit().putString(LATITUDE, latitude.toString()).apply()
                    mapPrefs.edit().putString(LONGITUDE, longitude.toString()).apply()
                    findNavController().navigate(R.id.action_mapFragment_to_home_graph)
                }


            }

            Log.d(TAG, "latitude: $latitude, longitude: $longitude")

            return true
        }
        return false
    }


    override fun longPressHelper(p: GeoPoint?): Boolean {
        return false
    }
}