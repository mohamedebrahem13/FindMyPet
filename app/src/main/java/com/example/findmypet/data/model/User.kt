package com.example.findmypet.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class User(
    val userId: String?=null,
    val email: String="",
    val nickname: String="",
    val phoneNumber: String="",
    val imagePath:String = "",
    var favoritePosts: List<String> = emptyList() // Add favorite posts attribute
): Parcelable