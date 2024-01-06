package com.example.findmypet.domain.usecase.firebaseUseCase.notification

import com.example.findmypet.data.repository.FirebaseCloudMessagingRepository
import javax.inject.Inject

class SendNotificationToUserUseCase @Inject constructor(
    private val fcmRepository: FirebaseCloudMessagingRepository
) {
    suspend fun sendNotificationToUser(title: String, body: String, receiverToken: String) {
        fcmRepository.sendNotificationToToken(title, body, receiverToken)
    }
}
