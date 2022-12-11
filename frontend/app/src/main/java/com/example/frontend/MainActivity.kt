package com.example.frontend

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.frontend.databinding.ActivityMainBinding
import com.example.frontend.databinding.EditTrashCardViewBinding
import com.example.frontend.databinding.NewTrashCardViewBinding
import com.example.frontend.databinding.TrashBinListViewBinding
import com.example.frontend.place.Place
import com.example.frontend.place.PlacesReader
import com.example.frontend.utils.PermissionUtils

class MainActivity :
    AppCompatActivity(),
    OnRequestPermissionsResultCallback,
    TrashCardAdapter.ItemListener,
    MapsFragment.MapFragmentListener
{
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var bindingCardListStub : TrashBinListViewBinding
    lateinit var bindingCardAddStub :  NewTrashCardViewBinding
    private lateinit var bindingCardEditStub :  EditTrashCardViewBinding

    lateinit var mapFragment: MapsFragment

    private var permissionDenied = false
    //private var contributionMode = false

    private lateinit var cardBinListView: View
    lateinit var cardBinAddView: View
    private lateinit var cardBinEditView: View
    private lateinit var contributionEnterView: View
    private lateinit var contributionExitView: View
    lateinit var contributionConfirmView: View

    private val places: List<Place> by lazy {
        PlacesReader(this).read()
    }

    var currentMode: Int = 0
    enum class ContributionMode(val value: Int) {
        EXIT(1), ENTER(0), CANCEL(2);

        companion object {
            fun create(x: Int): ContributionMode {
                return when (x) {
                    0 -> ENTER
                    1 -> EXIT
                    2 -> CANCEL
                    else -> throw IllegalStateException()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Create the and attach the map fragment
        mapFragment = MapsFragment()
        val fragManager = supportFragmentManager
        fragManager.commit{
            add(binding.mapsFragFrame.id, mapFragment)
        }

        /**
         *  Inflate binding of viewStubs in order to access components inside the layout
         */
        binding.stubBinList.setOnInflateListener {_, inflateId -> bindingCardListStub = TrashBinListViewBinding.bind(inflateId)}
        binding.stubBinAdd.setOnInflateListener {_, inflateId -> bindingCardAddStub = NewTrashCardViewBinding.bind(inflateId)}
        binding.stubBinEdit.setOnInflateListener {_, inflateId -> bindingCardEditStub = EditTrashCardViewBinding.bind(inflateId)}


        /**
         *  Set the layout resource file that should be used for the different viewStubs
         */
        // Main card item
        binding.stubBinList.layoutResource = R.layout.trash_bin_list_view
        binding.stubBinAdd.layoutResource = R.layout.new_trash_card_view
        binding.stubBinEdit.layoutResource = R.layout.edit_trash_card_view
        // Bottom buttons
        binding.stubEnterContribute.layoutResource = R.layout.trash_bottom_enter_view
        binding.stubExitContribute.layoutResource = R.layout.trash_bottom_contribute_view
        binding.stubConfirmContribute.layoutResource = R.layout.trash_bottom_confirm_view


        /**
         *  Inflate views that are used throughout the trash bin card
         */
        cardBinListView = binding.stubBinList.inflate()
        cardBinAddView = binding.stubBinAdd.inflate()
        cardBinEditView = binding.stubBinEdit.inflate()
        contributionEnterView = binding.stubEnterContribute.inflate()
        contributionExitView =  binding.stubExitContribute.inflate()
        contributionConfirmView = binding.stubConfirmContribute.inflate()

        /**
         *  Hide the viewStubs that are not used immediatly
         */
        contributionExitView.visibility = View.GONE
        contributionConfirmView.visibility = View.GONE
        cardBinAddView.visibility = View.GONE
        cardBinEditView.visibility = View.GONE

        val trashCardAdapter = TrashCardAdapter(places as ArrayList<Place>)
        bindingCardListStub.trashRecyclerView.adapter = trashCardAdapter
        bindingCardListStub.trashRecyclerView.adapter = trashCardAdapter
        bindingCardListStub.trashRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)

        //trashCardAdapter.setListener(this@MainActivity)

    }

    fun contributionModeTrigger(view: View?){
        when(ContributionMode.create(currentMode)){
            ContributionMode.ENTER -> {
                currentMode = ContributionMode.EXIT.value
                contributionEnterView.visibility = View.GONE
                contributionExitView.visibility = View.VISIBLE
                binding.textViewContribution?.visibility = View.VISIBLE
            }
            ContributionMode.EXIT -> {
                currentMode = ContributionMode.ENTER.value

                contributionEnterView.visibility = View.VISIBLE
                contributionExitView.visibility = View.GONE
                binding.textViewContribution?.visibility = View.INVISIBLE
                cardBinListView.visibility = View.VISIBLE
            }
            ContributionMode.CANCEL -> {
                currentMode = ContributionMode.EXIT.value
                cardBinListView.visibility = View.VISIBLE
                cardBinAddView.visibility = View.GONE

                contributionConfirmView.visibility = View.GONE
                contributionExitView.visibility = View.VISIBLE

            }
        }
        Log.d("ITM", "$currentMode")
    }

    fun addNewBin(view: View?){
        currentMode = ContributionMode.CANCEL.value
        Log.d("ITM", "$currentMode")

        cardBinAddView.visibility = View.VISIBLE
        cardBinListView.visibility = View.GONE
        contributionConfirmView.visibility = View.VISIBLE
        contributionExitView.visibility = View.GONE

        mapFragment.prepareNewBin()
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
            mapFragment.googleMap.isMyLocationEnabled = true
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

    override fun checkContributionMode(): Boolean {
        if(currentMode == ContributionMode.EXIT.value || currentMode == ContributionMode.CANCEL.value){
            return true
        }
        return false
    }
}