package com.goldouble.android.laundryday.db

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Exclude
import java.util.*

data class ReviewData(
        @get:Exclude var id: String = "",
        val laundry: DocumentReference? = null,
        val writer: DocumentReference? = null,
        val content: String = "",
        val rate: Float = 0f,
        val thumbs: List<String>? = null,
        val time: Date = Date(),
        @get:Exclude var nickname: String? = null
)