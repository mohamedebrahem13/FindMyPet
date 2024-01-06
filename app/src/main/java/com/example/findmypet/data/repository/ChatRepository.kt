package com.example.findmypet.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.example.findmypet.common.Constant
import com.example.findmypet.common.Constant.COLLECTION_PATH
import com.example.findmypet.common.Constant.ID
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
import kotlinx.coroutines.launch
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
                launch {
                    markMessagesAsRead(newMessages, user1Id, user2Id)
                    Log.v("messagesUnread","messages$newMessages")
                }
            }

            awaitClose {
                // Clean up the listener when the flow is closed
                listener.remove()
            }
        }
    }
    private suspend fun markMessagesAsRead(messages: List<Message>, user1Id: String, user2Id: String) {
        messages.forEach { message ->
            if (message.senderId == user2Id && message.receiverId == user1Id && !message.isRead) {
                // Update the message in the database using your update function (e.g., markMessageAsRead)
                markMessageAsRead(message)
            }
        }
    }
    private suspend fun markMessageAsRead(message: Message) {

        try {
            val messageDocRef = db.collection("Messages").document(message.messageId)
            messageDocRef.update("isRead", true).await()            // Update successful
        } catch (e: Exception) {
            // Handle any errors or log the error message
            Log.e(TAG, "Error updating message read status: $e")
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
                    user2Id = user2Id, lastMessage = messageText, lastMessageTimestamp = System.currentTimeMillis()
                )
                createConversationInFirestore(newConversation)
            }

            // Send the message
            val message = Message(
                messageId = "",
                channelId = channelId,
                senderId = user1Id,
                receiverId = user2Id,
                message = messageText,
                timestamp = System.currentTimeMillis(),
                isRead = false
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
                        it.imagePath,it.email,it.phone,conversation.lastMessage,conversation.lastMessageTimestamp
                    )
                    displayConversations.add(displayConversation)
                }
            }

            emit(displayConversations)
        }
    }

    override suspend fun getRecipientFCMToken(receiverId: String): String? {
        return try {
            val userDoc = db.collection(COLLECTION_PATH).document(receiverId).get().await()
            userDoc.getString("token")
        } catch (e: Exception) {
            null // Handle exceptions or return null if the token is not found
        }
    }

    override suspend fun checkUnreadMessages(receiverId: String,currentUserId:String): Boolean {
        val unreadMessages = db.collection("Messages")
            .whereEqualTo("receiverId", receiverId)
            .whereEqualTo("senderId", currentUserId)
            .whereEqualTo("isRead", false)
            .get()
            .await()

        return !unreadMessages.isEmpty
    }


    private suspend fun getUserDetails(userId: String): User? {
        return try {
            val userDoc = db.collection(COLLECTION_PATH).document(userId).get().await()

            if (userDoc.exists()) {
                val user = userDoc.data

                // Extract specific fields from the user document
                val id = user?.get(ID) as? String ?: ""
                val email = user?.get(Constant.E_MAIL) as? String ?: ""
                val nickname = user?.get(Constant.NICKNAME) as? String ?: ""
                val phoneNumber = user?.get(Constant.PHONE_NUMBER) as? String ?: ""
                val imagePath = user?.get(Constant.PROFILE_IMAGE_PATH) as? String ?: ""

                // Print the retrieved user details
                println("Retrieved User Details: ID: $id, Email: $email, Nickname: $nickname, Phone: $phoneNumber, ImagePath: $imagePath")

                // Construct and return a User object with the extracted fields
                User(id, email, nickname, phoneNumber, imagePath)
            } else {
                // Handle case where the user document doesn't exist
                null
            }
        } catch (e: Exception) {
            // Handle exceptions or return null if details not found
            e.printStackTrace()
            null
        }
    }


    private suspend fun sendMessageToFirestore(message: Message) {
        val messageData = hashMapOf(
            "channelId" to message.channelId,
            "senderId" to message.senderId,
            "receiverId" to message.receiverId,
            "message" to message.message,
            "timestamp" to message.timestamp,
            "isRead" to false
        )

        val db = FirebaseFirestore.getInstance()

        try {
            val docRef = db.collection("Messages").add(messageData).await()
            val generatedMessageId = docRef.id

            // Update the messageId field in the same document
            db.collection("Messages").document(docRef.id)
                .update("messageId", generatedMessageId)
                .await()

            // Update the last message in the conversation document with the new message ID
            updateLastMessageInConversation(message.channelId, message.message, message.timestamp)
        } catch (e: Exception) {
            // Handle failure
        }
    }


    // Update the last message in the conversation document
    private fun updateLastMessageInConversation(channelId: String, lastMessage: String, timestamp: Long) {
        val db = FirebaseFirestore.getInstance()

        // Assuming 'conversations' collection and each conversation has a document
        val conversationDocRef = db.collection("Conversations").document(channelId)

        val updateData = hashMapOf<String, Any>(
            "lastMessage" to lastMessage,
            "lastMessageTimestamp" to timestamp
        )

        conversationDocRef.update(updateData)
            .addOnSuccessListener {
                // Update successful
            }
            .addOnFailureListener { e ->
                // Handle any errors or log the error message
                Log.e(TAG, "Error updating last message: $e")
            }
    }


    private suspend fun checkConversationExists(channelId: String): Boolean {
        val conversationDocument = db.collection("Conversations").document(channelId).get().await()
        return conversationDocument.exists()
    }

    private suspend fun createConversationInFirestore(conversation: Conversation) {
        val conversationData = hashMapOf(
            "channelId" to conversation.channelId,
            "user1Id" to conversation.user1Id,
            "user2Id" to conversation.user2Id,
            "lastMessage" to conversation.lastMessage,
            "lastMessageTimestamp" to conversation.lastMessageTimestamp
        )

        db.collection("Conversations")
            .document(conversation.channelId) // Using channelId as the document ID
            .set(conversationData)
            .await()
    }
}
