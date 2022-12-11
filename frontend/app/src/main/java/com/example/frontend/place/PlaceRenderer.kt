package com.example.frontend.place

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.example.frontend.BitmapHelper
//import com.example.frontend.BitmapHelper
import com.example.frontend.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

class PlaceRenderer(private val context: Context, map: GoogleMap, clusterManager: ClusterManager<Place>) : DefaultClusterRenderer<Place>(context, map, clusterManager) {

    override fun getColor(clusterSize: Int): Int {
        val hueRange = 345f
        val sizeRange = 300f
        val size = clusterSize.toFloat().coerceAtMost(sizeRange)
        val hue = (sizeRange - size) * (sizeRange - size) / (sizeRange * sizeRange) * hueRange
        return Color.HSVToColor(
            floatArrayOf(
                hue, 0.67f, .95f
            )
        )
    }


    /**
     * The icon to use for each cluster item
     */
    private val trashIcon: BitmapDescriptor by lazy {
        val color = ContextCompat.getColor(context,
            R.color.primary
        )
        BitmapHelper.vectorToBitmap(
            context,
            R.drawable.ic_baseline_restore_from_trash_48,
            color
        )
    }


    override fun onBeforeClusterItemRendered(item: Place, markerOptions: MarkerOptions) {
        item.let {
            markerOptions.title(item.name)
                .position(it.latLng)
                .icon(trashIcon)
        }
    }

    override fun onClusterItemRendered(clusterItem: Place, marker: Marker) {
        marker.tag = clusterItem
    }
}