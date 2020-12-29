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
import java.text.SimpleDateFormat
import java.util.*

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
                textReviewTime.text = timeText(data.time)
                textReviewName.text = data.nickname
                textReviewContent.text = data.content
                ratingBarReview.rating = data.rate
            }
        }

        fun timeText(time: Date): String {
            val now = Date().time
            val date = time.time

            return when(val second = (now - date) / 1000) {
                in 0..60 -> "${second}초 전"
                in 60..3600 -> "${second / 60}분 전"
                in 3600..86400 -> "${second / 3600}시간 전"
                in 86400..2592000 -> "${second / 86400}일 전"
                else -> SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault()).format(time)
            }
        }
    }
}