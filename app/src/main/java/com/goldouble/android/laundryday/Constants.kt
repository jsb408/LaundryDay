package com.goldouble.android.laundryday

import android.content.res.Resources
import com.goldouble.android.laundryday.db.RealmLaundry
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.realm.Realm
import io.realm.RealmConfiguration
import java.util.*

//Local Constants
enum class Table(val id: String) {
    MEMBERS("MEMBERS"),
    LAUNDRY("LAUNDRY"),
    REVIEW("REVIEW")
}

enum class RealmTable(val table: String, val title: String) {
    RECENT("RecentView", "최근 본 세탁소"),
    BOOKMARK("Bookmarks", "찜한 세탁소")
}

fun kRealm(table: RealmTable) = kRealm(table.table)
fun kRealm(table: String) = Realm.getInstance(RealmConfiguration.Builder().name(table).build())!!

fun kAddBookamrk(storeId: String) {
    kRealm(RealmTable.BOOKMARK).apply {
        beginTransaction()
        createObject(RealmLaundry::class.java).apply {
            id = storeId
            time = Date()
        }
        commitTransaction()
    }
    kChangeBookmark()
}

fun kDeleteBookmark(storeId: String) {
    kRealm(RealmTable.BOOKMARK).apply {
        beginTransaction()
        where(RealmLaundry::class.java).equalTo("id", storeId).findAll().deleteAllFromRealm()
        commitTransaction()
    }
    kChangeBookmark()
}

fun kChangeBookmark() {
    kAuth.currentUser?.let {
        val bookmarkArray = arrayListOf<Map<String, Any>>()
        kRealm(RealmTable.BOOKMARK).where(RealmLaundry::class.java).findAll().forEach { data ->
            bookmarkArray.add(mapOf(
                    "id" to data.id,
                    "time" to data.time
            ))
        }

        kFirestore.collection(Table.MEMBERS.id).document(it.email!!).update("bookmarks", bookmarkArray)
    }
}

val kAuth = Firebase.auth
val kFirestore = Firebase.firestore

//Global Constants
fun kIntToDp(i: Int) = kFloatToDp(i.toFloat())
fun kFloatToDp(f: Float) = (f * Resources.getSystem().displayMetrics.density + 0.5f).toInt()