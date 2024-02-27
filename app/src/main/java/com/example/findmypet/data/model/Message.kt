package com.example.findmypet.data.model

import androidx.annotation.Keep

@Keep
data class Message(
    val messageId: String = "", // Unique ID for the message
    val channelId: String = "", // Unique ID for the conversation channel
    val senderId: String = "", // ID of the message sender
    val receiverId: String = "", // ID of the message receiver
    val message: String = "", // Content of the message
    val timestamp: Long = 0,// Timestamp for when the message was sent
    val isRead: Boolean = false
){
    constructor() : this("", "", "", "", "", 0, false)

}
