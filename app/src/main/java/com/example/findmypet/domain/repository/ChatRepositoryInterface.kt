package com.example.findmypet.domain.repository

import com.example.findmypet.data.model.DisplayConversation
import com.example.findmypet.data.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepositoryInterface {
    fun getMessagesForChannelRealTime(user2Id: String): Flow<List<Message>>
    suspend fun sendMessageAndInitiateChatIfNeeded(user2Id: String, messageText: String)
    suspend fun getAllConversationsWithUserDetailsForCurrentUser(): List<DisplayConversation>
    suspend fun getRecipientFCMToken(receiverId: String): String?
    suspend fun checkUnreadMessages(receiverId: String,currentUserId:String): Boolean

}