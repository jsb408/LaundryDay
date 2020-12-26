package com.goldouble.android.laundryday

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldouble.android.laundryday.adapter.MyStoreListRecyclerViewAdapter
import com.goldouble.android.laundryday.adapter.StoreListRecyclerViewAdapter
import com.goldouble.android.laundryday.databinding.ActivityStoreListBinding
import com.goldouble.android.laundryday.db.RealmLaundry
import com.naver.maps.geometry.LatLng

class StoreListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoreListBinding
    private lateinit var currentLatLng : LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = intent.getStringExtra("title") ?: "내근처 세탁소"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        currentLatLng = LatLng(intent.getDoubleExtra("lat", 37.4979), intent.getDoubleExtra("lng", 127.0276))

        binding.recyclerViewStoreList.adapter = if(intent.getStringExtra("type").isNullOrBlank()) StoreListRecyclerViewAdapter(currentLatLng)
        else MyStoreListRecyclerViewAdapter(currentLatLng, kRealm(intent.getStringExtra("type")!!).where(RealmLaundry::class.java).findAll().sortedByDescending { it.time })
        binding.recyclerViewStoreList.layoutManager = LinearLayoutManager(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        binding.recyclerViewStoreList.adapter = if(intent.getStringExtra("type").isNullOrBlank()) StoreListRecyclerViewAdapter(currentLatLng)
        else MyStoreListRecyclerViewAdapter(currentLatLng, kRealm(intent.getStringExtra("type")!!).where(RealmLaundry::class.java).findAll().sortedByDescending { it.time })
    }
}