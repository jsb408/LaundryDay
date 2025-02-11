package com.goldouble.android.laundryday.db

import android.location.Location
import com.google.firebase.firestore.GeoPoint
import com.naver.maps.geometry.LatLng
import java.util.*

data class LaundryData(
        val address: String = "",
        val address_array: List<String> = listOf(),
        val latLng: GeoPoint = GeoPoint(0.0, 0.0),
        val name: String = "",
        val number: String = "",
        val registered: Boolean = true,
        val time: Date = Date(),
        val type: String = "0001",
        val writer: String? = null
) {
        var id = ""

        fun distance(location: Location?) = distance(LatLng(location?.latitude ?: 0.0, location?.longitude ?: 0.0))
        fun distance(location: LatLng) = location.distanceTo(LatLng(latLng.latitude, latLng.longitude))
}