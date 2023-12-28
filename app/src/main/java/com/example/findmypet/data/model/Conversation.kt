package com.example.findmypet.data.model

data class Conversation(
    val channelId: String = "", // Unique ID for the conversation channel
    val user1Id: String = "", // ID of user 1 in the conversation
    val user2Id: String = "" // ID of user 2 in the conversation
)
