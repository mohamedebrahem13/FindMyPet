package com.example.findmypet.ui.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findmypet.common.MessageResource
import com.example.findmypet.data.model.Message
import com.example.findmypet.domain.usecase.firebaseUseCase.GetCurrentUserUidUseCase
import com.example.findmypet.domain.usecase.firebaseUseCase.chat.GetMessagesForChannelUseCase
import com.example.findmypet.domain.usecase.firebaseUseCase.chat.SendMessageAndInitiateChatIfNeededUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ChatViewModel @Inject constructor(private val getMessagesForChannelUseCase: GetMessagesForChannelUseCase,private val sendMessageAndInitiateChatIfNeededUseCase:SendMessageAndInitiateChatIfNeededUseCase,private val getCurrentUserUidUseCase: GetCurrentUserUidUseCase) : ViewModel() {
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
                // Handle the error, show a message, or perform other actions as needed
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