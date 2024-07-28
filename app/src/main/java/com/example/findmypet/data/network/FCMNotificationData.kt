package com.example.findmypet.data.network

data class FCMNotificationData(
    val title: String,
    val body: String,
    val image: String? = null // Optional image URL
)
