package com.goldouble.android.laundryday

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.UiThread
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.goldouble.android.laundryday.adapter.StoreListRecyclerViewAdapter
import com.goldouble.android.laundryday.databinding.ActivityMainBinding
import com.goldouble.android.laundryday.db.LaundryData
import com.goldouble.android.laundryday.db.NaverItem
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import ted.gun0912.clustering.naver.TedNaverClustering

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000

    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<CardView>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder
    private lateinit var locationSource: FusedLocationSource

    private var currentLocation = LatLng(37.4979, 127.0276)
    private var currentLocationName: String = "서초구"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.contentMain.bottomSheetMain.root).apply {
            halfExpandedRatio = 0.3f
            isDraggable = false
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        geocoder = Geocoder(this)

        supportActionBar?.hide()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.naverMapMapView) as MapFragment?
                ?: MapFragment.newInstance().also {
                    supportFragmentManager.beginTransaction().add(R.id.naverMapMapView, it).commit()
                }

        checkPermission(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
        fusedLocationClient.lastLocation.addOnSuccessListener {
            currentLocationName = geocoder.getFromLocation(it.latitude, it.longitude, 1).first().getAddressLine(0).split(" ")[2]
            Log.e("LOCATION", currentLocationName)
            currentLocation = LatLng(it)
            mapFragment.getMapAsync(this)
        }

        binding.contentMain.buttonMainMyPage.setOnClickListener {
            binding.drawerLayoutMain.openDrawer(binding.navMain)
        }

        binding.contentMain.bottomSheetMain.imageMainBookmark.setOnClickListener {
            (it as ImageView).setColorFilter(getColor(R.color.switchActivate))
        }

        kAuth.currentUser?.let {
            val statusText = "${it.displayName}님 반갑습니다"
            binding.drawerMain.textDrawerStatus.text = statusText
            binding.drawerMain.buttonDrawerSignOut.visibility = View.VISIBLE
            binding.drawerMain.buttonDrawerSignOut.setOnClickListener {
                AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
                    .setMessage("로그아웃하시겠습니까?")
                    .setPositiveButton("예") { _, _ ->
                        kAuth.signOut()
                        startActivity(Intent(this, MainActivity::class.java))
                        finishAffinity()
                    }
                    .setNegativeButton("아니오") { _, _ -> }
                    .show()
            }
        } ?: run {
            binding.drawerMain.textDrawerStatus.setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        naverMap.uiSettings.apply {
            isZoomControlEnabled = false
            isLocationButtonEnabled = true
        }

        naverMap.locationSource = locationSource

        kFirestore.collection("LAUNDRY").whereArrayContains("address_array", currentLocationName).get().addOnSuccessListener { docs ->
            val markerList = mutableListOf<NaverItem>()
            val laundryList = mutableListOf<LaundryData>()

            docs.forEach { doc ->
                val location = LatLng(doc.getGeoPoint("latLng")!!.latitude, doc.getGeoPoint("latLng")!!.longitude)

                val data = doc.toObject(LaundryData::class.java).apply {
                    distance = currentLocation.distanceTo(location)
                }

                laundryList.add(data)
                markerList.add(NaverItem(data))
            }

            StoreListRecyclerViewAdapter.data = laundryList.sortedBy { it.distance }

            naverMap.moveCamera(CameraUpdate.scrollTo(currentLocation))

            binding.contentMain.buttonMainList.setOnClickListener {
                startActivity(Intent(this, StoreListActivity::class.java))
            }

            TedNaverClustering.with<NaverItem>(this, naverMap).items(markerList).apply {
                customMarker {
                    Marker().apply {
                        icon = OverlayImage.fromResource(R.drawable.marker_coin)
                        width = 160
                        height = 160
                    }
                }

                markerClickListener {
                    binding.contentMain.bottomSheetMain.apply {
                        textMainName.text = it.data.name
                        textMainAddress.text = it.data.address

                        val distanceText = "${it.data.distance.toInt()}m"
                        textMainDistance.text = distanceText
                    }
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                }

                clusterClickListener {
                    naverMap.apply {
                        moveCamera(CameraUpdate.scrollTo(LatLng(it.position.latitude, it.position.longitude)).animate(CameraAnimation.Easing).finishCallback {
                            moveCamera(CameraUpdate.zoomIn().animate(CameraAnimation.Easing))
                        })
                    }
                }

                make()
            }
        }

        naverMap.setOnMapClickListener { _, _ ->
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if(!locationSource.isActivated) finish()
        }
    }

    private fun checkPermission(vararg permissions: String) {
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it.containsValue(false)) finish()
        }.launch(permissions)
    }
}