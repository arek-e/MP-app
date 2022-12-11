package com.example.frontend

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.RadioGroup.OnCheckedChangeListener
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.frontend.databinding.*
import com.example.frontend.place.Place
import com.example.frontend.place.PlaceRenderer
import com.example.frontend.place.PlacesReader
import com.example.frontend.place.Wastetypes
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textfield.TextInputLayout.OnEditTextAttachedListener
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
    private lateinit var binding: FragmentMapsBinding
    private lateinit var bindingCardAddStub :  NewTrashCardViewBinding
    private lateinit var contributionConfirmView: View


    var binCreationStarted = false
    private var newMarker: Marker? = null
    private var wastetypes: Wastetypes = Wastetypes()
    lateinit var name: String
    var switchFull: Boolean = false
    var switchDamaged: Boolean = false

    lateinit var googleMap: GoogleMap
    private val places: List<Place> by lazy {
        PlacesReader(requireContext()).read()
    }

    interface MapFragmentListener{
        fun checkContributionMode(): Boolean
    }
    private lateinit var mapFragmentListener: MapFragmentListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mapFragmentListener = context as MapFragmentListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mainActivity = activity as MainActivity
        binding = FragmentMapsBinding.inflate(inflater,container,false)
        bindingCardAddStub = mainActivity.bindingCardAddStub
        contributionConfirmView = mainActivity.contributionConfirmView


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        lifecycleScope.launchWhenCreated {
            // Get map
            googleMap = mapFragment.awaitMap()

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


            googleMap.setOnMarkerDragListener(object : OnMarkerDragListener {
                override fun onMarkerDragStart(arg0: Marker) {
                    Log.d("ITM", "onMarkerDragStart..." + arg0.position.latitude + "..." + arg0.position.longitude)
                }

                override fun onMarkerDragEnd(arg0: Marker) {
                    Log.d("ITM", "onMarkerDragEnd..." + arg0.position.latitude + "..." + arg0.position.longitude)
                    geoCodeAddress(arg0.position)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(arg0.position))
                }

                override fun onMarkerDrag(arg0: Marker) {
                    //Log.i("ITM", "onMarkerDrag...")
                }
            })

        }

        contributionConfirmView.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
            confirmBin(it)
        }

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

    fun prepareNewBin(){
        wasteTypeToggle(bindingCardAddStub.iconPaper, false)
        wasteTypeToggle(bindingCardAddStub.iconLiquid, false)
        wasteTypeToggle(bindingCardAddStub.iconGlass, false)
        wasteTypeToggle(bindingCardAddStub.iconOrganic, false)
        wasteTypeToggle(bindingCardAddStub.iconPlastic, false)
        wasteTypeToggle(bindingCardAddStub.iconMetal, false)

        bindingCardAddStub.editTextBinName.addTextChangedListener(object:TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //Log.d("ITM", "$p0")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //Log.d("ITM", "$p0")
            }

            override fun afterTextChanged(p0: Editable?) {
                Log.d("ITM", "$p0")
                name = p0.toString()
            }

        })

        bindingCardAddStub.switchFull.setOnCheckedChangeListener { _, toggle -> switchFull = toggle }
        bindingCardAddStub.switchDamaged.setOnCheckedChangeListener { _, toggle -> switchDamaged = toggle }
    }

    private fun wasteTypeToggle(imageView: ImageView, type: Boolean){
        var toggled: Boolean = type
        imageView.setOnClickListener{
            toggled = !toggled
            if (toggled){
                imageView.setColorFilter(ContextCompat.getColor(requireContext(), android.R.color.transparent), android.graphics.PorterDuff.Mode.SRC_ATOP);
            }else{
                imageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.disabled), android.graphics.PorterDuff.Mode.SRC_ATOP);
            }

            when(imageView.id){
                R.id.icon_paper -> wastetypes.paper = toggled
                R.id.icon_liquid -> wastetypes.liquid = toggled
                R.id.icon_glass -> wastetypes.glass = toggled
                R.id.icon_organic -> wastetypes.organic = toggled
                R.id.icon_plastic -> wastetypes.plastic = toggled
                R.id.icon_metal -> wastetypes.metal = toggled
            }

        }
    }

    private fun confirmBin(view: View){
        if (newMarker == null){
            val toast = Toast.makeText(requireContext(), "Click on map to place marker!", Toast.LENGTH_SHORT)
            toast.show()
            return
        }


        var newBin = Place(
            name,
            newMarker!!.position,
            geoCodeAddress(newMarker!!.position),
            isMissing = false,
            switchFull,
            switchDamaged,
            wastetypes
        )
        newMarker!!.title = name
        newMarker!!.isDraggable = false
        newMarker!!.tag = newBin

        Log.d("ITM", "$newBin")

        resetCreation()

    }

    private fun resetCreation(){
        newMarker = null
        bindingCardAddStub.editTextBinName.text = null
        wastetypes = Wastetypes()

        bindingCardAddStub.iconGlass.setColorFilter(ContextCompat.getColor(requireContext(), R.color.disabled), android.graphics.PorterDuff.Mode.SRC_ATOP)
        bindingCardAddStub.iconLiquid.setColorFilter(ContextCompat.getColor(requireContext(), R.color.disabled), android.graphics.PorterDuff.Mode.SRC_ATOP);
        bindingCardAddStub.iconMetal.setColorFilter(ContextCompat.getColor(requireContext(), R.color.disabled), android.graphics.PorterDuff.Mode.SRC_ATOP);
        bindingCardAddStub.iconOrganic.setColorFilter(ContextCompat.getColor(requireContext(), R.color.disabled), android.graphics.PorterDuff.Mode.SRC_ATOP);
        bindingCardAddStub.iconPlastic.setColorFilter(ContextCompat.getColor(requireContext(), R.color.disabled), android.graphics.PorterDuff.Mode.SRC_ATOP);
        bindingCardAddStub.iconPaper.setColorFilter(ContextCompat.getColor(requireContext(), R.color.disabled), android.graphics.PorterDuff.Mode.SRC_ATOP);

        bindingCardAddStub.switchFull.isChecked = false
        bindingCardAddStub.switchDamaged.isChecked = false

        val mainActivity = activity as MainActivity
        mainActivity.contributionModeTrigger(view)


    }

    fun geoCodeAddress(pos: LatLng): String{
        val geocoder: Geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses: List<Address> = geocoder.getFromLocation(pos.latitude,pos.latitude,10)
        Log.d("ITM", "$addresses")
        return addresses[0].getAddressLine(0)
    }

    private fun onMapClick(googleMap: GoogleMap,latLng: LatLng){
        if(!mapFragmentListener.checkContributionMode()) {
            Log.d("ITM", "Contribution mode not enabled!")
            return
        }

        if (!binCreationStarted){
            binCreationStarted = true
        }else{
        }
        if (newMarker != null) {
            //TODO Animation that bounces to more clearly show bin
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(newMarker!!.position))
        }else {
            val name: String = "New bin"

            newMarker = googleMap.addMarker {
                title(name)
                    .position(latLng)
                    .icon(trashIcon)
                    .draggable(true)
            }
        }



        // Set place as the tag on the marker object so it can be referenced within
        // MarkerInfoWindowAdapter
        //marker.tag = Place(name,latLng,addresses[0].getAddressLine(0),1.0F, wastetypes)


    }

    override fun onMapReady(googleMap: GoogleMap) {
    }
}