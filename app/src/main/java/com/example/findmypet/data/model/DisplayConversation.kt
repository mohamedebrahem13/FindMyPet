package com.example.findmypet.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class DisplayConversation(
    val channelId: String,
    val secondUserId: String,
    val secondUserName: String,
    val secondUserImage: String,
    val secondUserEmile: String,
    val secondUserPhone: String,
    val lastMessage: String = "",
    val lastMessageTimestamp: Long = 0
): Parcelable{
    constructor() : this("", "", "", "", "", "", "", 0)

}