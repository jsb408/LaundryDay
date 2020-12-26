package com.goldouble.android.laundryday.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.goldouble.android.laundryday.databinding.ActivityStoreDetailBinding
import com.goldouble.android.laundryday.databinding.ItemReviewBinding
import com.goldouble.android.laundryday.db.ReviewData
import java.text.DecimalFormat

class StoreReviewListRecyclerViewAdapter(private val parentBinding: ActivityStoreDetailBinding, options: FirestoreRecyclerOptions<ReviewData>)
    : FirestoreRecyclerAdapter<ReviewData, StoreReviewListRecyclerViewAdapter.ItemViewHolder>(options) {
    var totalRating = 0.0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoreReviewListRecyclerViewAdapter.ItemViewHolder, position: Int, model: ReviewData) {
        val countText = "${itemCount}건"
        parentBinding.textDetailReviewCount.text = countText

        totalRating += model.rate
        parentBinding.textDetailReviewAvg.text = DecimalFormat("0.0").format(totalRating / itemCount)

        model.id = snapshots.getSnapshot(position).id
        model.writer?.get()?.addOnSuccessListener {
            model.nickname = it.getString("nickname")
            holder.bindData(model)
        }
        holder.bindData(model)
    }

    inner class ItemViewHolder(private val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: ReviewData) {
            binding.apply {
                textReviewName.text = data.nickname
                textReviewContent.text = data.content
                ratingBarReview.rating = data.rate
            }
        }
    }
}