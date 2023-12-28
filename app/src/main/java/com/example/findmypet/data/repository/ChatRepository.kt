package com.example.findmypet.data.repository

import com.example.findmypet.common.Constant.COLLECTION_PATH
import com.example.findmypet.data.model.Conversation
import com.example.findmypet.data.model.DisplayConversation
import com.example.findmypet.data.model.Message
import com.example.findmypet.data.model.User
import com.example.findmypet.domain.repository.ChatRepositoryInterface
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatRepository@Inject constructor(private val db: FirebaseFirestore,private val firebaseAuth: FirebaseAuth):
    ChatRepositoryInterface {

    private fun generateChannelId(user1Id: String, user2Id: String): String {
        val sortedUserIds = listOf(user1Id, user2Id).sorted()
        return "${sortedUserIds[0]}_${sortedUserIds[1]}"
    }


    override fun getMessagesForChannelRealTime(user2Id: String): Flow<List<Message>> = callbackFlow {
        val currentUser = firebaseAuth.currentUser
        currentUser?.let { user ->
            val user1Id = user.uid
            val channelId = generateChannelId(user1Id, user2Id)

            val messagesCollection = db.collection("Messages")
                .whereEqualTo("channelId", channelId)

            val listener = messagesCollection.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e) // Close the flow in case of errors
                    return@addSnapshotListener
                }

                val newMessages = mutableListOf<Message>()
                snapshot?.forEach { doc ->
                    val message = doc.toObject(Message::class.java)
                    newMessages.add(message)
                }

                newMessages.sortBy { it.timestamp } // Sort messages by timestamp

                trySend(newMessages) // Try sending new messages to the flow
                    .isSuccess // Check if sending was successful
            }

            awaitClose {
                // Clean up the listener when the flow is closed
                listener.remove()
            }
        }
    }




    override suspend fun sendMessageAndInitiateChatIfNeeded(user2Id: String, messageText: String) {
        val currentUser = firebaseAuth.currentUser
        currentUser?.let { user ->
            val user1Id = user.uid
            val channelId = generateChannelId(user1Id, user2Id)

            // Check if the conversation exists
            val conversationExists = checkConversationExists(channelId)
            if (!conversationExists) {
                val newConversation = Conversation(
                    channelId = channelId,
                    user1Id = user1Id,
                    user2Id = user2Id
                )
                createConversationInFirestore(newConversation)
            }

            // Send the message
            val message = Message(
                channelId = channelId,
                senderId = user1Id,
                receiverId = user2Id,
                message = messageText,
                timestamp = System.currentTimeMillis()
            )
            sendMessageToFirestore(message)
        }
    }

    override fun getAllConversationsWithUserDetailsForCurrentUser(): Flow<List<DisplayConversation>> = flow {
        val currentUser = firebaseAuth.currentUser

        currentUser?.let { user ->
            val userId = user.uid

            val conversationsSnapshot1 = db.collection("Conversations")
                .whereEqualTo("user1Id", userId)
                .get()
                .await()

            val conversationsSnapshot2 = db.collection("Conversations")
                .whereEqualTo("user2Id", userId)
                .get()
                .await()

            val conversations1 = conversationsSnapshot1.documents.mapNotNull { document ->
                document.toObject(Conversation::class.java)
            }

            val conversations2 = conversationsSnapshot2.documents.mapNotNull { document ->
                document.toObject(Conversation::class.java)
            }

            val allConversations = conversations1 + conversations2

            val displayConversations = mutableListOf<DisplayConversation>()

            for (conversation in allConversations) {
                val secondUserId = if (userId == conversation.user1Id) {
                    conversation.user2Id
                } else {
                    conversation.user1Id
                }

                val userDetails = getUserDetails(secondUserId)

                userDetails?.let {
                    val displayConversation = DisplayConversation(
                        conversation.channelId,
                        secondUserId,
                        it.nickname,
                        it.Profile_image,it.email,it.phone
                    )
                    displayConversations.add(displayConversation)
                }
            }

            emit(displayConversations)
        }
    }


    suspend fun getUserDetails(userId: String): User? {
        return try {
            val userDoc = db.collection(COLLECTION_PATH).document(userId).get().await()
            userDoc.toObject(User::class.java)
        } catch (e: Exception) {
            // Handle exceptions or return null if details not found
            null
        }
    }


    private suspend fun sendMessageToFirestore(message: Message) {
        val messageData = hashMapOf(
            "channelId" to message.channelId,
            "senderId" to message.senderId,
            "receiverId" to message.receiverId,
            "message" to message.message,
            "timestamp" to message.timestamp
        )

        db.collection("Messages")
            .add(messageData)
            .await()
    }

    private suspend fun checkConversationExists(channelId: String): Boolean {
        val conversationDocument = db.collection("Conversations").document(channelId).get().await()
        return conversationDocument.exists()
    }

    private suspend fun createConversationInFirestore(conversation: Conversation) {
        val conversationData = hashMapOf(
            "channelId" to conversation.channelId,
            "user1Id" to conversation.user1Id,
            "user2Id" to conversation.user2Id
        )

        db.collection("Conversations")
            .document(conversation.channelId) // Using channelId as the document ID
            .set(conversationData)
            .await()
    }
}
