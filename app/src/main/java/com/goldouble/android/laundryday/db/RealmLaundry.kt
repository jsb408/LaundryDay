package com.goldouble.android.laundryday.db

import io.realm.RealmObject
import java.util.*

open class RealmLaundry(
    var id: String = "",
    var time: Date = Date()
) : RealmObject()
