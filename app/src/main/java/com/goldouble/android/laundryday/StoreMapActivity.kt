package com.goldouble.android.laundryday

import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import com.goldouble.android.laundryday.databinding.ActivityStoreMapBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage

class StoreMapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityStoreMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "지도보기"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.naverMapViewMap) as MapFragment?
            ?: MapFragment.newInstance().also {
                supportFragmentManager.beginTransaction().add(R.id.naverMapViewMap, it).commit()
            }

        mapFragment.getMapAsync(this)

        binding.textMapName.text = intent.getStringExtra("name")
        binding.textMapAddress.text = intent.getStringExtra("address")
        binding.textMapNumber.text = intent.getStringExtra("number")
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        val latLng = LatLng(intent.getDoubleExtra("lat", 0.0), intent.getDoubleExtra("lng", 0.0))

        Marker().apply {
            position = latLng
            map = naverMap
            icon = OverlayImage.fromResource(R.drawable.marker_coin)
            width = 160
            height = 160
        }

        naverMap.moveCamera(CameraUpdate.scrollTo(latLng))

        naverMap.uiSettings.apply {
            isZoomControlEnabled = false
            isScrollGesturesEnabled = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
        }

        return super.onOptionsItemSelected(item)
    }
}