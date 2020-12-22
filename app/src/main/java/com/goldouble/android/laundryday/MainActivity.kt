package com.goldouble.android.laundryday

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.UiThread
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.goldouble.android.laundryday.databinding.ActivityMainBinding
import com.goldouble.android.laundryday.databinding.ItemDrawerMainBinding
import com.goldouble.android.laundryday.db.LaundryData
import com.goldouble.android.laundryday.db.NaverItem
import com.goldouble.android.laundryday.db.RealmLaundry
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.firestore.GeoPoint
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import ted.gun0912.clustering.naver.TedNaverClustering
import java.text.DecimalFormat
import java.util.*
import kotlin.math.min

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    companion object {
        val laundryList = mutableListOf<LaundryData>()
    }

    private val LOCATION_PERMISSION_REQUEST_CODE = 1000

    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<CardView>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder
    private lateinit var locationSource: FusedLocationSource

    private var currentLocation = LatLng(37.4979, 127.0276)
    private var visitedLocationName = mutableListOf<String>()

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
        laundryList.clear()

        supportActionBar?.hide()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.naverMapViewMain) as MapFragment?
                ?: MapFragment.newInstance().also {
                    supportFragmentManager.beginTransaction().add(R.id.naverMapViewMain, it).commit()
                }

        checkPermission(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
        fusedLocationClient.lastLocation.addOnSuccessListener {
            currentLocation = LatLng(it)
            mapFragment.getMapAsync(this)
        }

        binding.contentMain.buttonMainMyPage.setOnClickListener {
            binding.drawerLayoutMain.openDrawer(binding.navMain)
            bindDrawer(locationSource.lastLocation)
            kRealm(RealmTable.RECENT).addChangeListener { bindDrawer(locationSource.lastLocation) }
        }

        binding.drawerMain.buttonDrawerBookmarksCount.setOnClickListener {
            startActivity(Intent(this, StoreListActivity::class.java)
                    .putExtra("title", RealmTable.BOOKMARK.title)
                    .putExtra("type", RealmTable.BOOKMARK.table)
                    .putExtra("lat", locationSource.lastLocation?.latitude)
                    .putExtra("lng", locationSource.lastLocation?.longitude))
        }

        binding.drawerMain.buttonDrawerRecentViewCount.setOnClickListener {
            startActivity(Intent(this, StoreListActivity::class.java)
                    .putExtra("title", RealmTable.RECENT.title)
                    .putExtra("type", RealmTable.RECENT.table)
                    .putExtra("lat", locationSource.lastLocation?.latitude)
                    .putExtra("lng", locationSource.lastLocation?.longitude))
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
            binding.drawerMain.buttonDrawerMyReview.setTextColor(getColor(R.color.addressTextColor))
            binding.drawerMain.buttonDrawerReport.setTextColor(getColor(R.color.addressTextColor))
        }
    }

    override fun onResume() {
        super.onResume()
        bindDrawer(locationSource.lastLocation)
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        naverMap.uiSettings.apply {
            isZoomControlEnabled = false
            isLocationButtonEnabled = true
        }

        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow

        naverMap.moveCamera(CameraUpdate.scrollTo(currentLocation))
        naverMap.moveCamera(CameraUpdate.zoomIn())

        naverMap.addOnLocationChangeListener {
            try {
                val locationName = geocoder.getFromLocation(it.latitude, it.longitude, 1).first().getAddressLine(0).split(" ")[2]
                Log.e("LOCATION", locationName)

                if (!visitedLocationName.contains(locationName)) {
                    visitedLocationName.add(locationName)
                    kFirestore.collection("LAUNDRY").whereArrayContains("address_array", locationName).get().addOnSuccessListener { docs ->
                        val markerList = mutableListOf<NaverItem>()

                        docs.forEach { doc ->
                            val data = doc.toObject(LaundryData::class.java).apply {
                                id = doc.id
                            }

                            laundryList.add(data)
                            markerList.add(NaverItem(data))
                        }

                        binding.contentMain.buttonMainList.setOnClickListener {
                            startActivity(Intent(this, StoreListActivity::class.java)
                                    .putExtra("lat", locationSource.lastLocation?.latitude)
                                    .putExtra("lng", locationSource.lastLocation?.longitude)
                            )
                        }

                        TedNaverClustering.with<NaverItem>(this, naverMap).items(markerList).apply {
                            customMarker {
                                Marker().apply {
                                    icon = OverlayImage.fromResource(R.drawable.marker_coin)
                                    width = 160
                                    height = 160
                                }
                            }

                            markerClickListener { naverItem ->
                                var isMarked = kRealm(RealmTable.BOOKMARK).where(RealmLaundry::class.java)
                                        .equalTo("id", naverItem.data.id).findAll().isNotEmpty()
                                binding.contentMain.bottomSheetMain.imageMainBookmark.setColorFilter(
                                        getColor(if(isMarked) R.color.switchActivate else R.color.addressTextColor)
                                )

                                binding.contentMain.bottomSheetMain.imageMainBookmark.setOnClickListener { icon ->
                                    if(isMarked) {
                                        (icon as ImageView).setColorFilter(getColor(R.color.addressTextColor))
                                        kDeleteBookmark(naverItem.data.id)
                                    } else {
                                        (icon as ImageView).setColorFilter(getColor(R.color.switchActivate))
                                        kAddBookamrk(naverItem.data.id)
                                    }

                                    isMarked = !isMarked
                                }

                                binding.contentMain.bottomSheetMain.apply {
                                    textMainName.text = naverItem.data.name
                                    textMainAddress.text = naverItem.data.address
                                    textMainDistance.text = distanceText(naverItem.data.distance(locationSource.lastLocation))
                                }
                                binding.contentMain.bottomSheetMain.cardViewMain.setOnClickListener { _ ->
                                    startActivity(Intent(binding.root.context, StoreDetailActivity::class.java)
                                            .putExtra("storeId", naverItem.data.id))
                                }
                                binding.contentMain.bottomSheetMain.imageMainPhone.setOnClickListener { _ ->
                                    startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${naverItem.data.number}")))
                                }
                                binding.contentMain.bottomSheetMain.imageMainMap.setOnClickListener { _ ->
                                    val latLng = naverItem.data.latLng
                                    try {
                                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("nmaps://route/public?dlat=${latLng.latitude}&dlng=${latLng.longitude}&dname=${naverItem.data.name}&appname=$packageName")))
                                    } catch (e: Exception) {
                                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=${latLng.latitude},${latLng.longitude}")))
                                    }
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
                }
            } catch (e: Exception) {
                e.printStackTrace()
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

    private fun geoPointToLatLng(geoPoint: GeoPoint?) = LatLng(geoPoint?.latitude ?: 0.0, geoPoint?.longitude ?: 0.0)

    private fun bindDrawer(location: Location?) = bindDrawer(location?.let { LatLng(it) } ?: LatLng(0.0, 0.0))

    private fun bindDrawer(location: LatLng) {
        val bookmarksData = kRealm(RealmTable.BOOKMARK).where(RealmLaundry::class.java).findAll()
        val bookmark = bookmarksData.maxByOrNull { it.time }
        val recentViewData = kRealm(RealmTable.RECENT).where(RealmLaundry::class.java).findAll().sortedByDescending { it.time }

        bookmark?.let { data ->
            val bookmarkCount = "${bookmarksData.size} >"
            binding.drawerMain.buttonDrawerBookmarksCount.text = bookmarkCount
            binding.drawerMain.layoutDrawerBookmarks.removeAllViews()

            val drawerBinding = ItemDrawerMainBinding.bind(layoutInflater.inflate(R.layout.item_drawer_main, null).also { view ->
                view.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                ).apply {
                    leftMargin = kIntToDp(20)
                    rightMargin = kIntToDp(20)
                    topMargin = kIntToDp(5)
                    bottomMargin = kIntToDp(5)
                }
            })

            kFirestore.collection(Table.LAUNDRY.id).document(data.id).get().addOnSuccessListener {
                drawerBinding.apply {
                    textDrawerName.text = it.getString("name")
                    textDrawerAddress.text = it.getString("address")
                    textDrawerDistance.text = distanceText(location.distanceTo(geoPointToLatLng(it.getGeoPoint("latLng"))))
                    cardViewItemDrawer.setOnClickListener { _ ->
                        startActivity(Intent(this@MainActivity, StoreDetailActivity::class.java)
                                .putExtra("storeId", it.id))
                    }
                    buttonDrawerCall.setOnClickListener { _ ->
                        startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${it.getString("number")}")))
                    }
                }
            }

            binding.drawerMain.layoutDrawerBookmarks.addView(drawerBinding.root)
        }

        val recentViewCount = "${recentViewData.size} >"
        binding.drawerMain.buttonDrawerRecentViewCount.text = recentViewCount
        binding.drawerMain.layoutDrawerRecentView.removeAllViews()

        for(i in 0..min(recentViewData.size - 1, 2)) {
            val drawerBinding = ItemDrawerMainBinding.bind(layoutInflater.inflate(R.layout.item_drawer_main, null).also { view ->
                view.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                ).apply {
                    leftMargin = kIntToDp(20)
                    rightMargin = kIntToDp(20)
                    topMargin = kIntToDp(5)
                    bottomMargin = kIntToDp(5)
                }
            })

            kFirestore.collection(Table.LAUNDRY.id).document(recentViewData[i].id).get().addOnSuccessListener {
                drawerBinding.apply {
                    textDrawerName.text = it.getString("name")
                    textDrawerAddress.text = it.getString("address")
                    textDrawerDistance.text = distanceText(location.distanceTo(geoPointToLatLng(it.getGeoPoint("latLng"))))
                    cardViewItemDrawer.setOnClickListener { _ ->
                        startActivity(Intent(this@MainActivity, StoreDetailActivity::class.java)
                                .putExtra("storeId", it.id))
                    }
                    buttonDrawerCall.setOnClickListener { _ ->
                        startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${it.getString("number")}")))
                    }
                }
            }

            binding.drawerMain.layoutDrawerRecentView.addView(drawerBinding.root)
        }
    }

    private fun checkPermission(vararg permissions: String) {
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it.containsValue(false)) finish()
        }.launch(permissions)
    }

    private fun distanceText(distance: Double): String = DecimalFormat(if(distance > 1000) "#,##0km" else "#,##0m").format(distance / (if(distance > 1000) 1000 else 1))
}