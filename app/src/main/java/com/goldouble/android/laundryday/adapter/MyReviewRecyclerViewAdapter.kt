package com.goldouble.android.laundryday.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.goldouble.android.laundryday.StoreDetailActivity
import com.goldouble.android.laundryday.databinding.ItemMyReviewBinding
import com.goldouble.android.laundryday.db.LaundryData
import com.goldouble.android.laundryday.db.ReviewData
import java.text.SimpleDateFormat
import java.util.*

class MyReviewRecyclerViewAdapter(options: FirestoreRecyclerOptions<ReviewData>) : FirestoreRecyclerAdapter<ReviewData, MyReviewRecyclerViewAdapter.ItemViewHolder>(options) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemMyReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int, model: ReviewData) {
        model.laundry?.get()?.addOnSuccessListener {
            val laundry = it.toObject(LaundryData::class.java)!!.apply {
                id = it.id
            }
            holder.bindData(laundry, model)
        }
    }

    inner class ItemViewHolder(private val binding: ItemMyReviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(laundry: LaundryData, review: ReviewData) {
            binding.apply {
                val nameText = "${laundry.name} >"
                textMyReviewName.text = nameText
                textMyReviewName.setOnClickListener {
                    ContextCompat.startActivity(binding.root.context, Intent(binding.root.context, StoreDetailActivity::class.java)
                            .putExtra("storeId", laundry.id), null)
                }

                ratingBarMyReview.rating = review.rate
                textMyReviewAddress.text = laundry.address
                textMyReviewContent.text = review.content
                textMyReviewDate.text = SimpleDateFormat("yyyy.MM.dd HH:mm 작성", Locale.getDefault()).format(review.time)
            }
        }
    }
}