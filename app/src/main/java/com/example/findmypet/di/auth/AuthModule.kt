package com.example.findmypet.di.auth

import com.example.findmypet.data.repository.EditProfileRepositoryImpl
import com.example.findmypet.data.repository.FirebaseAuthenticator
import com.example.findmypet.domain.repository.Authenticator
import com.example.findmypet.domain.repository.EditProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AuthModule {


    @Provides
    fun provideProfileImageRepository(): EditProfileRepository = EditProfileRepositoryImpl(
        storage = Firebase.storage,
        db = Firebase.firestore,
        firebaseAuth = Firebase.auth
    )


    @Provides
    @Singleton
    fun provideFirebaseAuthenticator(
        firebaseAuth: FirebaseAuth, firebaseFirestore: FirebaseFirestore,message:FirebaseMessaging
    ): Authenticator = FirebaseAuthenticator(firebaseAuth, firebaseFirestore,message)
}