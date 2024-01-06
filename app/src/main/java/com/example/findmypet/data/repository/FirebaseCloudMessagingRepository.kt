package com.example.findmypet.data.repository

import android.util.Log
import com.example.findmypet.data.network.FCMNotification
import com.example.findmypet.data.network.FCMNotificationData
import com.example.findmypet.domain.network.FCMService
import javax.inject.Inject

class FirebaseCloudMessagingRepository @Inject constructor(private val fcmService: FCMService) {

    suspend fun sendNotificationToTopic(title: String, body: String, topic: String) {
        Log.v("data in", "$title $body")
        val notificationData = FCMNotificationData(title, body)
        val notification = FCMNotification(notificationData, topic)

        try {
            val response = fcmService.sendNotification(notification)
            if (response.isSuccessful) {
                Log.v("notification send", "Notification sent successfully: ${response.code()} - ${response.message()}")
                // Notification sent successfully to the topic
            } else {
                Log.e("notification send", "Notification sending failed: ${response.code()} - ${response.message()}")
                // Handle unsuccessful response
            }
        } catch (e: Exception) {
            Log.e("notification send", "Exception while sending notification: ${e.message}")
            // Handle exceptions
        }
    }
    // Method to send notifications to a specific FCM token
    suspend fun sendNotificationToToken(title: String, body: String, token: String) {
        Log.v("data in", "$title $body")
        val notificationData = FCMNotificationData(title, body)
        val notification = FCMNotification(notificationData, token)

        try {
            val response = fcmService.sendNotification(notification)
            if (response.isSuccessful) {
                Log.v("notification send", "Notification sent successfully: ${response.code()} - ${response.message()}")
                // Notification sent successfully to the token
            } else {
                Log.e("notification send", "Notification sending failed: ${response.code()} - ${response.message()}")
                // Handle unsuccessful response
            }
        } catch (e: Exception) {
            Log.e("notification send", "Exception while sending notification: ${e.message}")
            // Handle exceptions
        }
    }

}