package com.example.findmypet.di.network

import com.example.findmypet.BuildConfig
import com.example.findmypet.common.Constant.BASE_URL
import com.example.findmypet.common.Constant.CONTENT_TYPE
import com.example.findmypet.data.repository.FirebaseCloudMessagingRepository
import com.example.findmypet.domain.network.FCMService
import com.example.findmypet.domain.usecase.firebaseUseCase.notification.SendNotificationToTopicUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val headersInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "key=${BuildConfig.API_KEY}")
                .addHeader("Content-Type", CONTENT_TYPE)
                .build()
            chain.proceed(request)
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(headersInterceptor)
            .build()
    }


    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideFirebaseCloudMessagingRepository(fcmService: FCMService): FirebaseCloudMessagingRepository {
        return FirebaseCloudMessagingRepository(fcmService)
    }

    @Provides
    fun provideSendNotificationToTopicUseCase(repository: FirebaseCloudMessagingRepository): SendNotificationToTopicUseCase {
        return SendNotificationToTopicUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideFCMService(retrofit: Retrofit): FCMService {
        return retrofit.create(FCMService::class.java)
    }
}