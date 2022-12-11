package com.example.frontend.place

import com.example.frontend.api.models.Wastetypes
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem


data class Place(
    var name: String,
    var latLng: LatLng,
    var address: String,
    var isMissing: Boolean = false,
    var isFull: Boolean,
    var isDamaged: Boolean,
    var wastetypes: Wastetypes

) : ClusterItem {
    override fun getPosition(): LatLng {
        return latLng
    }

    override fun getTitle(): String {
      return  name
    }

    override fun getSnippet(): String {
        return address
    }

}

