package com.goldouble.android.laundryday

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldouble.android.laundryday.adapter.StoreListRecyclerViewAdapter
import com.goldouble.android.laundryday.databinding.ActivityStoreListBinding

class StoreListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoreListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "내근처 세탁소"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.recyclerViewStoreList.adapter = StoreListRecyclerViewAdapter()
        binding.recyclerViewStoreList.layoutManager = LinearLayoutManager(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
        }

        return super.onOptionsItemSelected(item)
    }
}