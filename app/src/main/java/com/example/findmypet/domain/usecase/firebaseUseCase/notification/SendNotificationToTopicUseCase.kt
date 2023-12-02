package com.example.findmypet.domain.usecase.firebaseUseCase.notification

import com.example.findmypet.data.repository.FirebaseCloudMessagingRepository
import javax.inject.Inject

class SendNotificationToTopicUseCase @Inject constructor(
    private val fcmRepository: FirebaseCloudMessagingRepository
) {

    suspend fun sendNotificationToTopic(title: String, body: String, topic: String) {
        fcmRepository.sendNotificationToTopic(title, body, topic)
    }
}
