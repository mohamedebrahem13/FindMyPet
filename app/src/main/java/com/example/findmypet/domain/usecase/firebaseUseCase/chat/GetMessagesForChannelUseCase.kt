package com.example.findmypet.domain.usecase.firebaseUseCase.chat

import com.example.findmypet.data.model.Message
import com.example.findmypet.domain.repository.ChatRepositoryInterface
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMessagesForChannelUseCase @Inject constructor(private val chatRepository: ChatRepositoryInterface) {

    fun execute(user2Id: String): Flow<List<Message>> {
        return chatRepository.getMessagesForChannelRealTime(user2Id)
    }
}