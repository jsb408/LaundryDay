package com.goldouble.android.laundryday.adapter

import com.goldouble.android.laundryday.Table
import com.goldouble.android.laundryday.db.LaundryData
import com.goldouble.android.laundryday.db.RealmLaundry
import com.goldouble.android.laundryday.kFirestore
import com.naver.maps.geometry.LatLng

class MyStoreListRecyclerViewAdapter(point: LatLng, val data: List<RealmLaundry>) : StoreListRecyclerViewAdapter(point) {
    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: StoreListRecyclerViewAdapter.ItemViewHolder, position: Int) {
        kFirestore.collection(Table.LAUNDRY.id).document(data[position].id).get().addOnSuccessListener {
            holder.bindData(it.toObject(LaundryData::class.java)!!.apply { id = it.id })
        }
    }
}