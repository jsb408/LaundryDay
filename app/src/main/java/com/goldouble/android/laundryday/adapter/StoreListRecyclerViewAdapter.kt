package com.goldouble.android.laundryday.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.goldouble.android.laundryday.StoreMapActivity
import com.goldouble.android.laundryday.databinding.ItemStoreListBinding
import com.goldouble.android.laundryday.db.LaundryData

class StoreListRecyclerViewAdapter: RecyclerView.Adapter<StoreListRecyclerViewAdapter.ItemViewHolder>() {
    companion object {
        var data: List<LaundryData>? = null
    }

    override fun getItemCount(): Int = data!!.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreListRecyclerViewAdapter.ItemViewHolder {
        val binding = ItemStoreListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoreListRecyclerViewAdapter.ItemViewHolder, position: Int) {
        holder.bindData(data!![position])
    }

    inner class ItemViewHolder(val binding: ItemStoreListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: LaundryData) {
            binding.apply {
                textStoreItemName.text = data.name
                textStoreItemAddress.text = data.address

                val distanceText = "${data.distance.toInt()}m"
                textStoreItemDistance.text = distanceText

                cardViewStoreList.setOnClickListener {
                    startActivity(binding.root.context, Intent(binding.root.context, StoreMapActivity::class.java)
                        .putExtra("name", data.name)
                        .putExtra("address", data.address)
                        .putExtra("number", data.number)
                        .putExtra("lat", data.latLng.latitude)
                        .putExtra("lng", data.latLng.longitude), null)
                }
            }
        }
    }
}