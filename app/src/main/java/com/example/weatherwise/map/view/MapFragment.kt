package com.example.weatherwise.map.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.weatherwise.R
import com.example.weatherwise.SharedLocationViewModel
import com.example.weatherwise.dp.WeatherLocalDataSourceImpl
import com.example.weatherwise.home.viewmodel.HomeViewModel
import com.example.weatherwise.home.viewmodel.HomeViewModelFactory
import com.example.weatherwise.map.viewmodel.MapViewModel
import com.example.weatherwise.map.viewmodel.MapViewModelFactory
import com.example.weatherwise.model.FavoriteWeather
import com.example.weatherwise.model.WeatherRepoImpl
import com.example.weatherwise.network.WeatherRemoteDataSourceImpl
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class MapFragment : Fragment(), MapEventsReceiver{

    private lateinit var mapView : MapView
    private lateinit var mapViewModel: MapViewModel
    private lateinit var mapViewModelFactory: MapViewModelFactory
    private lateinit var btnConfirm : Button
    private lateinit var sharedLocationViewModel: SharedLocationViewModel
    private val TAG = "MapFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(context,androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext()))

        sharedLocationViewModel = ViewModelProvider(requireActivity()).get(SharedLocationViewModel::class.java)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mapView = view.findViewById(R.id.map_view)
        btnConfirm = view.findViewById(R.id.btn_confirm)


        mapViewModelFactory = MapViewModelFactory(
            WeatherRepoImpl.getInstance(
                WeatherRemoteDataSourceImpl.getInstance(),
                WeatherLocalDataSourceImpl(requireContext())

            )
        )

        mapViewModel = ViewModelProvider(this, mapViewModelFactory).get(MapViewModel::class.java)

        mapView.setBuiltInZoomControls(true)
        mapView.mapCenter

        mapView.setMultiTouchControls(true)

        val myLocationProvider = GpsMyLocationProvider(requireContext())
        val myLocationOverlay = MyLocationNewOverlay(myLocationProvider, mapView)
        mapView.overlays.add(myLocationOverlay)
        myLocationOverlay.enableMyLocation()
        myLocationOverlay.enableFollowLocation()

        val mapViewController = mapView.controller
        mapViewController.setZoom(20)


        val mapEventsOverlay = MapEventsOverlay(this)
        mapView.overlays.add(0, mapEventsOverlay)



    }

    override fun singleTapConfirmedHelper(geoPoint: GeoPoint?): Boolean {
        if (geoPoint != null) {

            mapView.overlays.removeAll { it is Marker }

            val marker = Marker(mapView)
            marker.position = geoPoint
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            mapView.overlays.add(marker)

            val mapViewController = mapView.controller
            mapViewController.setCenter(geoPoint)

            val latitude = geoPoint.latitude
            val longitude = geoPoint.longitude

            sharedLocationViewModel.updateLocation(latitude, longitude)

            Log.d(TAG, "latitude from shared view model: ${sharedLocationViewModel.latitude.value} ")
            Log.d(TAG, "longitude from shared view model: ${sharedLocationViewModel.longitude.value} ")

            val fav = FavoriteWeather(lat =latitude, lon = longitude, timezone = "Cairo")

            btnConfirm.setOnClickListener {
                findNavController().navigate(R.id.action_mapFragment2_to_favoriteFragment)
                mapViewModel.insertFavoriteWeather(fav)
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