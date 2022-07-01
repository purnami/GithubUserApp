package com.purnami.githubuserapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Profile (
    val id: String,
    val userName: String,
    val avatar: String,
    val followers: Int,
    val following:Int,
    val company: String,
    val blog: String
):Parcelable