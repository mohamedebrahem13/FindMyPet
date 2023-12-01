package com.example.findmypet.di.postrepo

import com.example.findmypet.data.remote.firestore.FirebasePostDataSource
import com.example.findmypet.data.repository.PostRepositoryImpl
import com.example.findmypet.domain.datasource.RemotePostDataSource
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
    fun provideRemotePostDataSource(
        firestore: FirebaseFirestore,
        storage: FirebaseStorage,
        firebaseAuth: FirebaseAuth
    ): RemotePostDataSource {
        return FirebasePostDataSource(storage,firestore , firebaseAuth)
    }



    @Provides
    @Singleton
    fun providePostRepository(
        remotePostDataSource: RemotePostDataSource
    ): PostRepository = PostRepositoryImpl(remotePostDataSource)


}