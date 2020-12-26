package com.goldouble.android.laundryday.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.goldouble.android.laundryday.*
import com.goldouble.android.laundryday.databinding.ItemStoreListBinding
import com.goldouble.android.laundryday.db.LaundryData
import com.goldouble.android.laundryday.db.RealmLaundry
import com.naver.maps.geometry.LatLng

class StoreListRecyclerViewAdapter(val latLng: LatLng) : RecyclerView.Adapter<StoreListRecyclerViewAdapter.ItemViewHolder>() {
    private val data = MainActivity.laundryList.sortedBy { it.distance(latLng) }

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

                var isMarked = kRealm(RealmTable.BOOKMARK).where(RealmLaundry::class.java).equalTo("id", data.id).findAll().isNotEmpty()
                imageStoreItemFavorite.setColorFilter(getColor(binding.root.context, if(isMarked) R.color.switchActivate else R.color.addressTextColor))

                imageStoreItemFavorite.setOnClickListener { icon ->
                    if(isMarked) {
                        (icon as ImageView).setColorFilter(getColor(binding.root.context, R.color.addressTextColor))
                        kDeleteBookmark(data.id)
                    } else {
                        (icon as ImageView).setColorFilter(getColor(binding.root.context, R.color.switchActivate))
                        kAddBookamrk(data.id)
                    }

                    isMarked = !isMarked
                }

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