package com.example.petme.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class User(
    val email: String,
    val nickname: String,
    val phoneNumber: String,
    val imagePath:String = ""
): Parcelable