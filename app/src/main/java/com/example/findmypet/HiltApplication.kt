package com.example.findmypet

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.os.Build
import android.util.Log
import com.example.findmypet.common.Constant
import com.example.findmypet.common.Constant.channelDescription
import com.example.findmypet.common.Constant.channelId
import com.example.findmypet.common.Constant.channelName
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@HiltAndroidApp
class HiltApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createChannel()
        subscribeToTopic(Constant.Topic)
    }

    private fun subscribeToTopic(topic: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                FirebaseMessaging.getInstance().subscribeToTopic(topic).await()
                Log.d(TAG, "Subscribed to $topic topic")
            } catch (e: Exception) {
                Log.e(TAG, "Subscription to $topic topic failed: ${e.message}")
            }
        }
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = channelId
            val channelName = channelName
            val channelDescription = channelDescription
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)

            Log.d("NotificationChannel", "Channel created")
        }
    }



}
