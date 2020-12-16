package com.goldouble.android.laundryday.db

import ted.gun0912.clustering.clustering.TedClusterItem
import ted.gun0912.clustering.geometry.TedLatLng

data class NaverItem(val data: LaundryData) : TedClusterItem {
    override fun getTedLatLng(): TedLatLng {
        return TedLatLng(data.latLng.latitude, data.latLng.longitude)
    }
}