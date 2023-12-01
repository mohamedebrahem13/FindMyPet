package com.example.findmypet.di

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseUser() = Firebase.auth
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        val firestore = Firebase.firestore

        // Disable offline persistence directly during Firestore initialization
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false)
            .build()

        firestore.firestoreSettings = settings
        return firestore
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage() = Firebase.storage


}