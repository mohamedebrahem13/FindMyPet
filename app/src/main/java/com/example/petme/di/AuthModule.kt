package com.example.petme.di

import com.example.petme.data.repository.FirebaseAuthenticator
import com.example.petme.data.repository.ProfileImageRepositoryImpl
import com.example.petme.domain.repository.Authenticator
import com.example.petme.domain.repository.ProfileImageRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
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
class AuthModule {





    @Provides
    fun provideProfileImageRepository(): ProfileImageRepository = ProfileImageRepositoryImpl(
        storage = Firebase.storage,
        db = Firebase.firestore,
        firebaseAuth = Firebase.auth
    )


    @Provides
    @Singleton
    fun provideFirebaseAuthenticator(
        firebaseAuth: FirebaseAuth, firebaseFirestore: FirebaseFirestore
    ): Authenticator = FirebaseAuthenticator(firebaseAuth, firebaseFirestore)
}