package com.example.findmypet.di

import com.example.findmypet.data.local.room.LocalUserDataSource
import com.example.findmypet.data.repository.PostRepositoryImpl
import com.example.findmypet.domain.datasource.LocalPostDataSource
import com.example.findmypet.domain.datasource.RemotePostDataSource
import com.example.findmypet.domain.repository.PostRepository
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
        remotePostDataSource: RemotePostDataSource,
        localPostDataSource: LocalPostDataSource,
        localUserDataSource: LocalUserDataSource
    ): PostRepository = PostRepositoryImpl( localPostDataSource,remotePostDataSource, localUserDataSource)


}