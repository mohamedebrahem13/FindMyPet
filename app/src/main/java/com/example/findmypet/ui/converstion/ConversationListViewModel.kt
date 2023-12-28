package com.example.findmypet.ui.converstion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findmypet.data.model.DisplayConversation
import com.example.findmypet.domain.usecase.firebaseUseCase.chat.GetAllConversationsForCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
            getAllConversationsForCurrentUserUseCase.execute().collect { conversationList ->
                _conversations.value = conversationList
            }
        }
    }
}