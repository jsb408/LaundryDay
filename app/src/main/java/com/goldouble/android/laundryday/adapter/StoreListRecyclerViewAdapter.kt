package com.goldouble.android.laundryday.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.goldouble.android.laundryday.MainActivity
import com.goldouble.android.laundryday.StoreDetailActivity
import com.goldouble.android.laundryday.databinding.ItemStoreListBinding
import com.goldouble.android.laundryday.db.LaundryData
import com.naver.maps.geometry.LatLng

class StoreListRecyclerViewAdapter(val latLng: LatLng) : RecyclerView.Adapter<StoreListRecyclerViewAdapter.ItemViewHolder>() {
    val data = MainActivity.laundryList.sortedBy { it.distance(latLng) }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreListRecyclerViewAdapter.ItemViewHolder {
        val binding = ItemStoreListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoreListRecyclerViewAdapter.ItemViewHolder, position: Int) {
        holder.bindData(data[position])
    }

    inner class ItemViewHolder(val binding: ItemStoreListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: LaundryData) {
            binding.apply {
                textStoreItemName.text = data.name
                textStoreItemAddress.text = data.address

                val distanceText = "${data.distance(latLng).toInt()}m"
                textStoreItemDistance.text = distanceText

                cardViewStoreList.setOnClickListener {
                    startActivity(binding.root.context, Intent(binding.root.context, StoreDetailActivity::class.java)
                        .putExtra("storeId", data.id), null)
                }

                imageStoreItemCall.setOnClickListener {
                    startActivity(it.context, Intent(Intent.ACTION_DIAL, Uri.parse("tel:${data.number}")), null)
                }
            }
        }
    }
}