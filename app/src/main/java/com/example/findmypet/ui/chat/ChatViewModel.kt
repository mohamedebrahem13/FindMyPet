package com.example.findmypet.ui.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findmypet.common.MessageResource
import com.example.findmypet.data.model.Message
import com.example.findmypet.domain.usecase.firebaseUseCase.GetCurrentUserUidUseCase
import com.example.findmypet.domain.usecase.firebaseUseCase.chat.CheckUnreadMessagesUseCase
import com.example.findmypet.domain.usecase.firebaseUseCase.chat.GetMessagesForChannelUseCase
import com.example.findmypet.domain.usecase.firebaseUseCase.chat.SendMessageAndInitiateChatIfNeededUseCase
import com.example.findmypet.domain.usecase.firebaseUseCase.notification.GetRecipientFCMTokenUseCase
import com.example.findmypet.domain.usecase.firebaseUseCase.notification.SendNotificationToUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ChatViewModel @Inject constructor(private val getMessagesForChannelUseCase: GetMessagesForChannelUseCase, private val sendMessageAndInitiateChatIfNeededUseCase:SendMessageAndInitiateChatIfNeededUseCase, private val getCurrentUserUidUseCase: GetCurrentUserUidUseCase, private val checkUnreadMessagesUseCase: CheckUnreadMessagesUseCase, private val getRecipientFCMTokenUseCase: GetRecipientFCMTokenUseCase,private val sendNotificationToUserUseCase: SendNotificationToUserUseCase
) : ViewModel() {
    private val _messageFlow = MutableSharedFlow<MessageResource>()
    val messageFlow: SharedFlow<MessageResource> get() = _messageFlow

    private val _messagesState = MutableStateFlow<List<Message>>(emptyList())
    val messagesState: StateFlow<List<Message>> get() = _messagesState




    var uid: String = ""


    init {
        setUid()
    }

    fun sendMessageAndInitiate(user2Id: String, messageText: String) {
        viewModelScope.launch {
            try {
                sendMessageAndInitiateChatIfNeededUseCase.execute(user2Id, messageText)

                _messageFlow.emit(MessageResource.Success("Message sent successfully"))
                // Delay sending the notification
                delay(30000) // 30 seconds delay (adjust as needed)

                // Check for unread messages after a delay
                val hasUnreadMessages = checkUnreadMessagesUseCase(user2Id)

                if (hasUnreadMessages) {
                    val recipientToken = getRecipientFCMTokenUseCase.getRecipientFCMToken(user2Id)

                    recipientToken?.let {
                        val notificationTitle = "New Message"
                        val notificationBody = "You have received a new message!  $messageText"

                        // Send the notification
                        sendNotificationToUserUseCase.sendNotificationToUser(
                            notificationTitle,
                            notificationBody,
                            it
                        )
                    }
                }
            } catch (e: Exception) {
                _messageFlow.emit(MessageResource.Error("Failed to send message", e))
            }
        }
    }


    private fun setUid() {
        viewModelScope.launch {
            try {
                uid = getCurrentUserUidUseCase()
                Log.v("uid", "user uid$uid ")
            } catch (e: Exception) {
                Log.e(
                    "ChatViewModel",
                    "Error in getCurrentUserUidUseCase in ChatViewModel: ${e.message}"
                )
            }

        }


    }
     fun refreshMessages(user2Id: String) {
        viewModelScope.launch {
            getMessagesForChannelUseCase.execute(user2Id)
                .collect { newMessages ->
                    _messagesState.value = newMessages
                }
        }
    }
}