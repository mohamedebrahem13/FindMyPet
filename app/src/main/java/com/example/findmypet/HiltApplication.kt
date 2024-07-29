package com.example.findmypet

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.util.Log
import com.example.findmypet.common.Constant
import com.example.findmypet.common.Constant.TOPIC
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@HiltAndroidApp
class HiltApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        subscribeToTopic()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun subscribeToTopic() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                FirebaseMessaging.getInstance().subscribeToTopic(TOPIC).await()
                Log.d(TAG, "Subscribed to $TOPIC topic")
            } catch (e: Exception) {
                Log.e(TAG, "Subscription to $TOPIC topic failed: ${e.message}")
            }
        }
    }

    private fun createNotificationChannels() {
        // Create the new pet notifications channel
        val petChannel = NotificationChannel(
            Constant.PET_CHANNEL_ID,
            Constant.PET_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = Constant.PET_CHANNEL_DESCRIPTION
        }

        // Create the chat notifications channel
        val chatChannel = NotificationChannel(
            Constant.CHAT_CHANNEL_ID,
            Constant.CHAT_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = Constant.CHAT_CHANNEL_DESCRIPTION
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(petChannel)
        notificationManager?.createNotificationChannel(chatChannel)

        Log.d("NotificationChannel", "Channels created")
    }

}
