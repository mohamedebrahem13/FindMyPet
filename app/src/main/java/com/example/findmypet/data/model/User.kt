package com.example.findmypet.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class User(
    val id: String?=null,
    val email: String="",
    val nickname: String="",
    val phone: String="",
    val imagePath:String = "",
    var favoritePosts: List<String> = emptyList() // Add favorite posts attribute
): Parcelable{
    constructor() : this("", "", "", "", "", emptyList())

}