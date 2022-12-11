package com.example.frontend.place

import com.google.android.gms.maps.model.LatLng

data class PlaceResponse(
    val geometry: Geometry,
    val name: String,
    val vicinity: String,
    val isMissing: Boolean,
    val isFull: Boolean,
    val isDamaged: Boolean,
    val wastetypes: Wastetypes
) {

    data class Geometry(
        val location: GeometryLocation
    )

    data class GeometryLocation(
        val lat: Double,
        val lng: Double
    )
}

fun PlaceResponse.toPlace(): Place = Place(
    name = name,
    latLng = LatLng(geometry.location.lat, geometry.location.lng),
    address = vicinity,
    isMissing = isMissing,
    isFull = isFull,
    isDamaged = isDamaged,
    wastetypes = wastetypes
)