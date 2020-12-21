package com.goldouble.android.laundryday

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldouble.android.laundryday.adapter.StoreListRecyclerViewAdapter
import com.goldouble.android.laundryday.databinding.ActivityStoreListBinding
import com.naver.maps.geometry.LatLng

class StoreListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoreListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "내근처 세탁소"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val currentLatLng = LatLng(intent.getDoubleExtra("lat", 37.4979), intent.getDoubleExtra("lng", 127.0276))

        binding.recyclerViewStoreList.adapter = StoreListRecyclerViewAdapter(currentLatLng)
        binding.recyclerViewStoreList.layoutManager = LinearLayoutManager(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
        }

        return super.onOptionsItemSelected(item)
    }
}