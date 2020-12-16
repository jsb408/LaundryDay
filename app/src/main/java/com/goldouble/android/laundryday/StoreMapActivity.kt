package com.goldouble.android.laundryday

import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import com.goldouble.android.laundryday.databinding.ActivityStoreMapBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback

class StoreMapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityStoreMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "지도보기"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.textMapName.text = intent.getStringExtra("name")
        binding.textMapAddress.text = intent.getStringExtra("address")
        binding.textMapNumber.text = intent.getStringExtra("number")
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        naverMap.moveCamera(CameraUpdate.scrollTo(LatLng(
            intent.getIntExtra("lat", 0).toDouble(),
            intent.getIntExtra("lng", 0).toDouble())
        ))

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