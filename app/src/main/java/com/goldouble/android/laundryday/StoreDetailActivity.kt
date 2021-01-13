package com.goldouble.android.laundryday

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.goldouble.android.laundryday.adapter.StoreReviewListRecyclerViewAdapter
import com.goldouble.android.laundryday.databinding.ActivityStoreDetailBinding
import com.goldouble.android.laundryday.db.RealmLaundry
import com.goldouble.android.laundryday.db.ReviewData
import com.google.firebase.firestore.Query
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import java.util.*

class StoreDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoreDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        supportActionBar?.title = "세탁소 정보"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val itemId = intent.getStringExtra("storeId")!!
        Log.d("DETAIL", intent.getStringExtra("storeId").toString())
        
        kFirestore.collection(Table.LAUNDRY.id).document(itemId).get().addOnSuccessListener {
            kRealm(RealmTable.RECENT).apply {
                beginTransaction()
                where(RealmLaundry::class.java).equalTo("id", it.id).findFirst()?.let {
                    it.time = Date()
                } ?: run {
                    createObject(RealmLaundry::class.java).apply {
                        id = it.id
                        time = Date()
                    }
                }
                commitTransaction()
            }

            binding.buttonDetailWriteReview.setOnClickListener { _ ->
                kAuth.currentUser?.let { _ ->
                    startActivity(Intent(this, WriteReviewActivity::class.java)
                        .putExtra("storeName", it.getString("name"))
                        .putExtra("storeId", itemId))
                } ?: run {
                    AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert)
                        .setTitle("로그인이 필요합니다")
                        .setMessage("로그인 화면으로 들어가시겠습니까?")
                        .setPositiveButton("예") { _, _ ->
                            startActivity(Intent(this, LoginActivity::class.java))
                        }
                        .setNegativeButton("아니오") { _, _ -> }
                        .show()
                }
            }

            binding.apply {
                textDetailName.text = it.getString("name")
                textDetailType.text = if(it.getString("type") == "0001") "셀프빨래방" else "세탁소"
                textDetailNumber.text = it.getString("number")
                textDetailAddress.text = it.getString("address")

                var isMarked = kRealm(RealmTable.BOOKMARK).where(RealmLaundry::class.java)
                        .equalTo("id", itemId).findAll().isNotEmpty()
                imageDetailFavorite.setColorFilter(
                        getColor(if(isMarked) R.color.favoriteButtonColor else R.color.addressTextColor)
                )

                imageDetailFavorite.setOnClickListener { icon ->
                    if(isMarked) {
                        (icon as ImageView).setColorFilter(getColor(R.color.addressTextColor))
                        kDeleteBookmark(itemId)
                    } else {
                        (icon as ImageView).setColorFilter(getColor(R.color.favoriteButtonColor))
                        kAddBookamrk(itemId)
                    }

                    isMarked = !isMarked
                }

                val query = kFirestore.collection(Table.REVIEW.id).whereEqualTo("laundry", it.reference).orderBy("time", Query.Direction.DESCENDING)
                val options = FirestoreRecyclerOptions.Builder<ReviewData>().setQuery(query, ReviewData::class.java).build()
                val reviewListAdapter = StoreReviewListRecyclerViewAdapter(binding, options)

                recyclerViewDetailReview.apply {
                    adapter = reviewListAdapter
                    layoutManager = LinearLayoutManager(this@StoreDetailActivity)
                }

                reviewListAdapter.startListening()

                textDetailNumber.setOnClickListener { _ ->
                    startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${it.getString("number")}")))
                }

                val mapFragment = supportFragmentManager.findFragmentById(R.id.naverMapViewDetail) as MapFragment?
                    ?: MapFragment.newInstance().also {
                        supportFragmentManager.beginTransaction().add(R.id.naverMapViewDetail, it).commit()
                    }

                mapFragment.getMapAsync { naverMap ->
                    val latLng = LatLng(it.getGeoPoint("latLng")!!.latitude, it.getGeoPoint("latLng")!!.longitude)

                    naverMap.uiSettings.apply {
                        isZoomControlEnabled = false
                        isScaleBarEnabled = false
                        isScrollGesturesEnabled = false
                        isZoomGesturesEnabled = false
                    }
                    Marker().apply {
                        position = latLng
                        map = naverMap
                        icon = OverlayImage.fromResource(R.drawable.marker_coin)
                        width = 160
                        height = 160
                    }
                    naverMap.moveCamera(CameraUpdate.scrollTo(latLng))

                    naverMap.setOnMapClickListener { _, _ ->
                        startActivity(
                            Intent(binding.root.context, StoreMapActivity::class.java)
                                .putExtra("name", it.getString("name"))
                                .putExtra("address", it.getString("address"))
                                .putExtra("number", it.getString("number"))
                                .putExtra("lat", it.getGeoPoint("latLng")!!.latitude)
                                .putExtra("lng", it.getGeoPoint("latLng")!!.longitude)
                        )
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
        }

        return super.onOptionsItemSelected(item)
    }
}