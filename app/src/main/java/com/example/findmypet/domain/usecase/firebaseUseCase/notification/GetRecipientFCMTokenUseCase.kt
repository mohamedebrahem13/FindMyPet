package com.example.findmypet.domain.usecase.firebaseUseCase.notification

import com.example.findmypet.domain.repository.ChatRepositoryInterface
import javax.inject.Inject

class GetRecipientFCMTokenUseCase @Inject constructor(
    private val chatRepository: ChatRepositoryInterface
) {
    suspend fun getRecipientFCMToken(receiverId: String): String? {
        return chatRepository.getRecipientFCMToken(receiverId)
    }
}
