package com.example.findmypet.data.model

import androidx.annotation.Keep

@Keep
data class Conversation(
    val channelId: String = "", // Unique ID for the conversation channel
    val user1Id: String = "", // ID of user 1 in the conversation
    val user2Id: String = "", // ID of user 2 in the conversation
    val lastMessage: String = "", // Content of the last message
    val lastMessageTimestamp: Long = 0 // Timestamp for the last message
){
    constructor() : this("", "", "", "", 0)
}