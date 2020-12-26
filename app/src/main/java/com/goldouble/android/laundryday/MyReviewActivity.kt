package com.goldouble.android.laundryday

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.goldouble.android.laundryday.adapter.MyReviewRecyclerViewAdapter
import com.goldouble.android.laundryday.databinding.ActivityMyReviewBinding
import com.goldouble.android.laundryday.db.ReviewData
import com.google.firebase.firestore.Query

class MyReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyReviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "내가 쓴 리뷰"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val query = kFirestore.collection(Table.REVIEW.id)
                .whereEqualTo("writer", kFirestore.collection(Table.MEMBERS.id).document(kAuth.currentUser!!.email!!))
                .orderBy("time", Query.Direction.DESCENDING)
        val options = FirestoreRecyclerOptions.Builder<ReviewData>().setQuery(query, ReviewData::class.java).build()
        val reviewListAdapter = MyReviewRecyclerViewAdapter(options)

        binding.recyclerViewMyReview.adapter = reviewListAdapter
        binding.recyclerViewMyReview.layoutManager = LinearLayoutManager(this)

        reviewListAdapter.startListening()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
        }

        return super.onOptionsItemSelected(item)
    }
}