package com.goldouble.android.laundryday

import android.content.res.Resources
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.realm.Realm
import io.realm.RealmConfiguration

//Local Constants
enum class Table(val id: String) {
    MEMBERS("MEMBERS"),
    LAUNDRY("LAUNDRY"),
    REVIEW("REVIEW")
}

enum class RealmTable(val table: String) {
    RECENT("RecentView"),
    BOOKMARK("Bookmakrs")
}

fun kRealm(table: RealmTable) = Realm.getInstance(RealmConfiguration.Builder().name(table.table).build())

val kAuth = Firebase.auth
val kFirestore = Firebase.firestore

//Global Constants

fun kIntToDp(i: Int) = (i * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
fun kFloatToDp(f: Float) = (f * Resources.getSystem().displayMetrics.density + 0.5f).toInt()