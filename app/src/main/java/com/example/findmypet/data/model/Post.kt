package com.example.findmypet.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.ServerTimestamp
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.Date

@Keep
@Parcelize
data class Post(
    @SerializedName("pet_name")
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
):Parcelable{

    constructor() : this(
        "", "", "", "", null, "", null, null, null
    )
}
