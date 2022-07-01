package com.purnami.githubuserapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Notes (
    val id:String,
    val notes:String
): Parcelable
