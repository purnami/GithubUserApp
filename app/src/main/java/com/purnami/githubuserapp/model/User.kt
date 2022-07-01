package com.purnami.githubuserapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User (
    val id: String,
    val userName: String,
    val avatar: String,
    val linkUser: String
):Parcelable