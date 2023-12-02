package com.example.findmypet.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.findmypet.R
import com.example.findmypet.activities.MainActivity
import com.example.findmypet.common.Constant.channelId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.from?.let { from ->
            // Message was sent to the "new_pet" topic
            Log.d("GET_NOTIFICATION", "Received message from 'new_pet' topic")

            // Extract notification data from the message payload
            val notification = remoteMessage.notification

            // Extract title and body from the notification payload
            val title = notification?.title ?: "Default Title"
            val body = notification?.body ?: "Default Body"

            // Log the extracted title and body
            Log.v("ReceivedNotification", "Title: $title, Body: $body")

            // Create a notification with a pending intent
            createNotification(title, body)
        }
    }



    @RequiresApi(Build.VERSION_CODES.S)
    private fun createNotification(title: String, body: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create an intent for the notification
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        // Build the notification
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.pet1)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val uniqueNotificationId = System.currentTimeMillis().toInt()

        // Show the notification
        notificationManager.notify(uniqueNotificationId, notification)
    }
}
