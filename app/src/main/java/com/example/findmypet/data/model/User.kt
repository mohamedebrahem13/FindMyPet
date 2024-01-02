package com.example.findmypet.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class User(
    val id: String?=null,
    val email: String="",
    val nickname: String="",
    val phone: String="",
    val imagePath:String = "",
    var favoritePosts: List<String> = emptyList() // Add favorite posts attribute
): Parcelable