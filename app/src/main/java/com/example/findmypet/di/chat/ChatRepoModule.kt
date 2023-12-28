package com.example.findmypet.di.chat

import com.example.findmypet.data.repository.ChatRepository
import com.example.findmypet.domain.repository.ChatRepositoryInterface
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ChatRepoModule {

    @Provides
    @Singleton
    fun provideChatRepositoryInterface(db: FirebaseFirestore, firebaseAuth: FirebaseAuth): ChatRepositoryInterface = ChatRepository(db, firebaseAuth)
    }




