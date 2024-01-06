package com.example.findmypet.domain.usecase.firebaseUseCase.chat

import com.example.findmypet.domain.repository.ChatRepositoryInterface
import javax.inject.Inject

class CheckUnreadMessagesUseCase @Inject constructor(
    private val chatRepository: ChatRepositoryInterface
) {
    suspend operator fun invoke(receiverId: String,currentUserId:String): Boolean {
        return chatRepository.checkUnreadMessages(receiverId,currentUserId)
    }
}