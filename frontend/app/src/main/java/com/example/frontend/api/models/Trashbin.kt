package com.example.frontend.api.models

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import com.google.maps.android.clustering.ClusterItem

data class Trashbin(
    @SerializedName("name")
    var name: String,
    @SerializedName("position")
    var latLng: LatLng,
    @SerializedName("address")
    var address: String,
    @SerializedName("missing")
    var isMissing: Boolean = false,
    @SerializedName("full")
    var isFull: Boolean,
    @SerializedName("damaged")
    var isDamaged: Boolean,
    @SerializedName("wastetypes")
    var wastetypes: Wastetypes
): ClusterItem {
    override fun getPosition(): LatLng {
        return latLng
    }

    override fun getTitle(): String? {
        return name
    }

    override fun getSnippet(): String? {
        return address
    }

}

data class TrashbinResponse (
    @SerializedName("id")
    val id: Int
)

