package com.example.findmypet.domain.network

import com.example.findmypet.common.Constant.CONTENT_TYPE
import com.example.findmypet.common.Constant.SERVER_KEY
import com.example.findmypet.data.network.FCMNotification
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

// Retrofit interface with headers using defined constants
interface FCMService {
    @Headers("Authorization: key=$SERVER_KEY", "Content-Type: $CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun sendNotification(@Body notification: FCMNotification): Response<ResponseBody>
}