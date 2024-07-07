package com.example.findmypet.domain.usecase.firebaseUseCase.chat

import com.example.findmypet.data.model.DisplayConversation
import com.example.findmypet.domain.repository.ChatRepositoryInterface
import javax.inject.Inject

class GetAllConversationsForCurrentUserUseCase @Inject constructor(
    private val chatRepository: ChatRepositoryInterface
) {

    suspend fun execute(): List<DisplayConversation> {
        return chatRepository.getAllConversationsWithUserDetailsForCurrentUser()
    }
}