package com.goldouble.android.laundryday

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

enum class kTable(val id: String) {
    MEMBERS("MEMBERS"),
    LAUNDRY("LAUNDRY"),
    REVIEW("REVIEW")
}

val kAuth = Firebase.auth
val kFirestore = Firebase.firestore