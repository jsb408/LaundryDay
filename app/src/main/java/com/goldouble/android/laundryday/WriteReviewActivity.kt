package com.goldouble.android.laundryday

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.goldouble.android.laundryday.databinding.ActivityWriteReviewBinding
import java.util.*

class WriteReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWriteReviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "리뷰쓰기"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.textWriteName.text = intent.getStringExtra("storeName")!!

        binding.buttonWriteSubmit.setOnClickListener {
            if(binding.ratingBarWrite.rating == 0f)
                Toast.makeText(this, "별점을 매겨주세요", Toast.LENGTH_SHORT).show()
            else if(binding.editTextWriteContent.text.isEmpty())
                Toast.makeText(this, "리뷰를 입력해주세요", Toast.LENGTH_SHORT).show()
            else {
                binding.loadingLayout.root.visibility = View.VISIBLE
                window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                kFirestore.collection(Table.LAUNDRY.id).document(intent.getStringExtra("storeId")!!).collection(Table.REVIEW.id).add(
                    hashMapOf(
                        "writer" to kFirestore.collection(Table.MEMBERS.id).document(kAuth.currentUser!!.email!!),
                        "time" to Date(),
                        "content" to binding.editTextWriteContent.text.toString(),
                        "rate" to binding.ratingBarWrite.rating
                    )
                ).addOnCompleteListener {
                    binding.loadingLayout.root.visibility = View.GONE
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }.addOnSuccessListener {
                    finish()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
        }

        return super.onOptionsItemSelected(item)
    }
}