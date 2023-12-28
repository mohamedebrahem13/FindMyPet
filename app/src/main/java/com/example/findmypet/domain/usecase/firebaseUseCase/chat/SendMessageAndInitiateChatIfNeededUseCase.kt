package com.example.findmypet.domain.usecase.firebaseUseCase.chat

import com.example.findmypet.domain.repository.ChatRepositoryInterface
import javax.inject.Inject

class SendMessageAndInitiateChatIfNeededUseCase @Inject constructor(private val chatRepository: ChatRepositoryInterface) {

    suspend fun execute(user2Id: String, messageText: String) {
        chatRepository.sendMessageAndInitiateChatIfNeeded(user2Id, messageText)
    }
}