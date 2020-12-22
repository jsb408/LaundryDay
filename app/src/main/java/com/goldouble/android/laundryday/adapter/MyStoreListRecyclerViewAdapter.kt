package com.goldouble.android.laundryday.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.goldouble.android.laundryday.*
import com.goldouble.android.laundryday.databinding.ItemStoreListBinding
import com.goldouble.android.laundryday.db.LaundryData
import com.goldouble.android.laundryday.db.RealmLaundry
import com.naver.maps.geometry.LatLng

class MyStoreListRecyclerViewAdapter(val latLng: LatLng, val data: List<RealmLaundry>) : RecyclerView.Adapter<MyStoreListRecyclerViewAdapter.ItemViewHolder>() {
    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemStoreListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        kFirestore.collection(Table.LAUNDRY.id).document(data[position].id).get().addOnSuccessListener {
            holder.bindData(it.toObject(LaundryData::class.java)!!.apply { id = it.id })
        }
    }

    inner class ItemViewHolder(val binding: ItemStoreListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: LaundryData) {
            binding.apply {
                textStoreItemName.text = data.name
                textStoreItemAddress.text = data.address

                val distanceText = "${data.distance(latLng).toInt()}m"
                textStoreItemDistance.text = distanceText

                var isMarked = kRealm(RealmTable.BOOKMARK).where(RealmLaundry::class.java)
                        .equalTo("id", data.id).findAll().isNotEmpty()
                imageStoreItemFavorite.setColorFilter(
                        ContextCompat.getColor(binding.root.context, if (isMarked) R.color.switchActivate else R.color.addressTextColor)
                )

                imageStoreItemFavorite.setOnClickListener { icon ->
                    if(isMarked) {
                        (icon as ImageView).setColorFilter(ContextCompat.getColor(binding.root.context, R.color.addressTextColor))
                        kDeleteBookmark(data.id)
                    } else {
                        (icon as ImageView).setColorFilter(ContextCompat.getColor(binding.root.context, R.color.switchActivate))
                        kAddBookamrk(data.id)
                    }

                    isMarked = !isMarked
                }

                cardViewStoreList.setOnClickListener {
                    ContextCompat.startActivity(binding.root.context, Intent(binding.root.context, StoreDetailActivity::class.java)
                            .putExtra("storeId", data.id), null)
                }

                imageStoreItemCall.setOnClickListener {
                    ContextCompat.startActivity(it.context, Intent(Intent.ACTION_DIAL, Uri.parse("tel:${data.number}")), null)
                }
            }
        }
    }
}