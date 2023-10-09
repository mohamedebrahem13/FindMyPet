package com.example.petme.di

import com.example.petme.data.repository.FirebaseAuthenticator
import com.example.petme.domain.repository.Authenticator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AuthModule {

    @Provides
    @Singleton
    fun provideFirebaseAuthenticator(
        firebaseAuth: FirebaseAuth, firebaseFirestore: FirebaseFirestore
    ): Authenticator = FirebaseAuthenticator(firebaseAuth, firebaseFirestore)
}