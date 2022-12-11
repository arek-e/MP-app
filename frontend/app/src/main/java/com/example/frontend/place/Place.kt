package com.example.frontend.place

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem


data class Place(
    val name: String,
    val latLng: LatLng,
    val address: String,
    val rating: Float,
    val wastetypes: Wastetypes

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

data class Wastetypes(
    val organic: Boolean,
    val metal: Boolean,
    val glass: Boolean,
    val liquid: Boolean,
    val paper: Boolean,
    val plastic: Boolean

)
