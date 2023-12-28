package com.example.findmypet.domain.usecase.firebaseUseCase.chat

import com.example.findmypet.data.model.DisplayConversation
import com.example.findmypet.domain.repository.ChatRepositoryInterface
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllConversationsForCurrentUserUseCase @Inject constructor(
    private val chatRepository: ChatRepositoryInterface
) {

    fun execute(): Flow<List<DisplayConversation>> {
        return chatRepository.getAllConversationsWithUserDetailsForCurrentUser()
    }
}