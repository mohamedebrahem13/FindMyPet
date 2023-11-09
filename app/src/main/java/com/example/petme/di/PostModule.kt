package com.example.petme.di

import com.example.petme.data.repository.FirebasePostRepository
import com.example.petme.domain.repository.PostRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class PostModule {


    @Provides
    fun providePostRepository(): PostRepository = FirebasePostRepository(
        db = Firebase.firestore,
        storage = Firebase.storage,
        firebaseAuth = Firebase.auth


    )

}