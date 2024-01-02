package com.example.findmypet.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


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
): Parcelable