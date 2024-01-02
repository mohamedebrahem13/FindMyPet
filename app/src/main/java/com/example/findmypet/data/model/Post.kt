package com.example.findmypet.data.model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Post(
    val pet_name: String = "",
    val pet_description: String = "",
    val pet_age: String = "",
    val pet_gender: String = "",
    var imageUrls: List<String>? = null,
    val pet_location: String = "",
    var postId: String? = null,
    var user: User? = null,
    @ServerTimestamp
    val timestamp: Date? = null
):Parcelable
