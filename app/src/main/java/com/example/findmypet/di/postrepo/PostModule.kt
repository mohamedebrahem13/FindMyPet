package com.example.findmypet.di.postrepo

import com.example.findmypet.data.repository.PostRepositoryImpl
import com.example.findmypet.domain.repository.PostRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PostModule {

    @Provides
    @Singleton
    fun providePostRepository(
        firestore: FirebaseFirestore,
        storage: FirebaseStorage,
        firebaseAuth: FirebaseAuth
    ): PostRepository = PostRepositoryImpl(storage,firestore,firebaseAuth)


}