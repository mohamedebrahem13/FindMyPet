package com.example.findmypet.data.network

data class FCMNotification(
    val notification: FCMNotificationData,
    val to: String // Device token here
)