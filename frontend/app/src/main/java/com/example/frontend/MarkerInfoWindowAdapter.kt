package com.example.frontend

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.utils.widget.ImageFilterView
import com.example.frontend.place.Place
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class MarkerInfoWindowAdapter(private val context: Context): GoogleMap.InfoWindowAdapter{

    override fun getInfoContents(p0: Marker): View? {
        // First the tag is retrieved
        val place = p0.tag as? Place ?: return null

        val view = LayoutInflater.from(context).inflate(R.layout.marker_info_contents, null)

        view.findViewById<ImageFilterView>(R.id.imageFilterView_organic).imageTintList = if (place.rating <= 1) ColorStateList.valueOf(Color.RED) else ColorStateList.valueOf(Color.BLUE)
        view.findViewById<ImageFilterView>(R.id.imageFilterView_metal).imageTintList = if (place.rating <= 2) ColorStateList.valueOf(Color.RED) else ColorStateList.valueOf(Color.BLUE)
        view.findViewById<ImageFilterView>(R.id.imageFilterView_liquid).imageTintList = if (place.rating <= 3) ColorStateList.valueOf(Color.RED) else ColorStateList.valueOf(Color.BLUE)
        view.findViewById<ImageFilterView>(R.id.imageFilterView_glass).imageTintList = if (place.rating <= 4) ColorStateList.valueOf(Color.RED) else ColorStateList.valueOf(Color.BLUE)
        view.findViewById<TextView>(R.id.text_view_title).text = place.name
        view.findViewById<TextView>(R.id.text_view_address).text = place.address

        return view
    }

    override fun getInfoWindow(p0: Marker): View? {
        return null
    }

}