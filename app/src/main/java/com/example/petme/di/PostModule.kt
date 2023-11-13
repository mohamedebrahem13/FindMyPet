package com.example.petme.di

import com.example.petme.data.local.room.LocalUserDataSource
import com.example.petme.data.repository.PostRepositoryImpl
import com.example.petme.domain.datasource.LocalPostDataSource
import com.example.petme.domain.datasource.RemotePostDataSource
import com.example.petme.domain.repository.PostRepository
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