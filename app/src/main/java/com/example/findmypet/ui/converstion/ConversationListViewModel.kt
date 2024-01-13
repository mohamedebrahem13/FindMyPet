package com.example.findmypet.ui.converstion

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findmypet.data.model.DisplayConversation
import com.example.findmypet.domain.usecase.firebaseUseCase.chat.GetAllConversationsForCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class ConversationListViewModel @Inject constructor(
    private val getAllConversationsForCurrentUserUseCase: GetAllConversationsForCurrentUserUseCase
) : ViewModel() {

    private val _conversations = MutableStateFlow<List<DisplayConversation>>(emptyList())
    val conversations: StateFlow<List<DisplayConversation>> = _conversations

    init {
        fetchConversations()
    }

    private fun fetchConversations() {
        viewModelScope.launch {
            getAllConversationsForCurrentUserUseCase.execute()
                .catch { exception ->
                    // Handle the exception here, you can log it or perform other actions
                    Log.e("conversations", "Error fetching conversations$exception")
                    // Update UI or notify the user about the error
                    // For example, you could set a default value or show an error message
                    _conversations.value = emptyList() // Set an empty list or handle differently
                }
                .collect { conversationList ->
                    _conversations.value = conversationList
                }
        }
    }
}