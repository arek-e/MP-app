package com.example.frontend

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.frontend.databinding.ActivityMainBinding
import com.example.frontend.place.Place
import com.example.frontend.place.PlacesReader
import com.example.frontend.utils.PermissionUtils
import com.google.android.gms.maps.MapFragment

class MainActivity :
    AppCompatActivity(),
    OnRequestPermissionsResultCallback,
    TrashCardAdapter.ItemListener
{
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    lateinit var mapFragment: MapsFragment
    private var permissionDenied = false
    private val places: List<Place> by lazy {
        PlacesReader(this).read()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        mapFragment = MapsFragment()
        val fragManager = supportFragmentManager
        fragManager.commit{
            add(binding.mapsFragFrame.id, mapFragment)
        }


        val trashCardAdapter = TrashCardAdapter(places as ArrayList<Place>)
        binding.trashRecyclerView.adapter = trashCardAdapter
        binding.trashRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)

        trashCardAdapter.setListener(this@MainActivity)

    }


    @SuppressLint("MissingPermission")
    private fun enableMyLocation(){
        // 1. Permissions are granted -> enable location
        if(ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ){
            mapFragment.mMap.isMyLocationEnabled = true
            return
        }

        // 2.
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            PermissionUtils.RationaleDialog.newInstance(
                LOCATION_PERMISSION_REQUEST_CODE, true
            ).show(supportFragmentManager, "dialog")
            return
        }

        // 3. Otherwise, request permission
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE){
            // TODO Refactor deprecated method
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }

        if (PermissionUtils.isPermissionGranted(
                permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION
            ) || PermissionUtils.isPermissionGranted(
                permissions, grantResults, Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation()
        } else {
            // Permission was denied. Display an error message
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true
        }
    }


    override fun onResumeFragments() {
        if (permissionDenied){
            showMissingPermissionError()
            permissionDenied = false
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private fun showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog.newInstance(true).show(supportFragmentManager, "dialog")
    }

    companion object {
        /**
         * Request code for location permission request.
         *
         * @see .onRequestPermissionsResult
         */
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    // Method is called when the listener is triggered from the recycle view item
    override fun onItemClicked(place: Place, position: Int) {
        // Run code when optional item inside the card is clicked
    }
}