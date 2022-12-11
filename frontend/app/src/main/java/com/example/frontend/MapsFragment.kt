package com.example.frontend

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.frontend.databinding.FragmentMapsBinding
import com.example.frontend.place.Place
import com.example.frontend.place.PlaceRenderer
import com.example.frontend.place.PlacesReader
import com.example.frontend.place.Wastetypes

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.ktx.addMarker
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.awaitMapLoad
import java.util.*

class MapsFragment :
    Fragment(),
    OnMapReadyCallback,
    GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener
{
    lateinit var binding: FragmentMapsBinding
    lateinit var mMap: GoogleMap
    private val places: List<Place> by lazy {
        PlacesReader(requireContext()).read()
    }

    interface MapFragmentListener{
        fun checkContributionMode(): Boolean
    }
    lateinit var mapFragmentListener: MapFragmentListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mapFragmentListener = context as MapFragmentListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapsBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        lifecycleScope.launchWhenCreated {
            // Get map
            val googleMap = mapFragment.awaitMap()

            // Wait for map to finish loading
            googleMap.awaitMapLoad()

            // Ensure all places are visible in the map
            val bounds = LatLngBounds.builder()
            places.forEach { bounds.include(it.latLng) }
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 20))

            addClusteredMarkers(googleMap)
            googleMap.setInfoWindowAdapter(MarkerInfoWindowAdapter(requireContext()))

            googleMap.setOnMapClickListener {
                onMapClick(googleMap, it)
            }
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    private val trashIcon: BitmapDescriptor by lazy {
        val color = ContextCompat.getColor(requireContext(), R.color.primary)
        BitmapHelper.vectorToBitmap(requireContext(), R.drawable.ic_baseline_restore_from_trash_24, color)
    }

    /**
     * Adds markers to the map. These markers won't be clustered.
     */
    private fun addMarkers(googleMap: GoogleMap) {
        places.forEach { place ->
            val marker = googleMap.addMarker {
                title(place.name)
                    .position(place.latLng)
                    .icon(trashIcon)
            }
            // Set place as the tag on the marker object so it can be referenced within
            // MarkerInfoWindowAdapter
            marker.tag = place
        }

    }

    /**
     * Adds markers to the map with clustering support.
     */
    private fun addClusteredMarkers(googleMap: GoogleMap) {
        // Create the ClusterManager class and set the custom renderer.
        val clusterManager = ClusterManager<Place>(requireContext(), googleMap)
        clusterManager.renderer =
            PlaceRenderer(
                requireContext(),
                googleMap,
                clusterManager
            )

        // Set custom info window adapter
        clusterManager.markerCollection.setInfoWindowAdapter(MarkerInfoWindowAdapter(requireContext()))

        // Add the places to the ClusterManager.
        clusterManager.addItems(places)
        clusterManager.cluster()

        // When the camera starts moving, change the alpha value of the marker to translucent
        googleMap.setOnCameraMoveStartedListener {
            clusterManager.markerCollection.markers.forEach { it.alpha = 0.3f }
            clusterManager.clusterMarkerCollection.markers.forEach { it.alpha = 0.3f }
        }

        // Set ClusterManager as the OnCameraIdleListener so that it
        // can re-cluster when zooming in and out.
        googleMap.setOnCameraIdleListener {
            // When the camera stops moving, change the alpha value back to opaque
            clusterManager.markerCollection.markers.forEach { it.alpha = 1.0f }
            clusterManager.clusterMarkerCollection.markers.forEach { it.alpha = 1.0f }

            clusterManager.onCameraIdle()

        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(context, "MyLocation button clicked", Toast.LENGTH_SHORT)
            .show()
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false
    }

    override fun onMyLocationClick(location: Location) {
        Toast.makeText(context, "Current location:\n$location", Toast.LENGTH_LONG)
            .show()
    }

    private fun onMapClick(googleMap: GoogleMap,latLng: LatLng){

        if(mapFragmentListener.checkContributionMode()){
            val name: String = "Test"
            val geocoder: Geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses: List<Address> = geocoder.getFromLocation(latLng.latitude,latLng.latitude,10)
            val wastetypes: Wastetypes = Wastetypes(
                organic = false,
                metal = false,
                glass = false,
                liquid = false,
                paper = false,
                plastic = false
            )

            Log.d("ITM", "$addresses")


            val marker = googleMap.addMarker {
                title(name)
                    .position(latLng)
                    .icon(trashIcon)
                    .draggable(true)
            }

            // Set place as the tag on the marker object so it can be referenced within
            // MarkerInfoWindowAdapter
            marker.tag = Place(name,latLng,addresses[0].getAddressLine(0),1.0F, wastetypes)
        }else {
            Log.d("ITM", "Contribution mode not enabled!")
        }


    }
}