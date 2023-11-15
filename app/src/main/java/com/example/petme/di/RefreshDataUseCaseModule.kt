package com.example.petme.di

import com.example.petme.domain.repository.PostRepository
import com.example.petme.domain.usecase.firebaseUseCase.worker.RefreshDataUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RefreshDataUseCaseModule {

    @Provides
    @Singleton
    fun provideRefreshDataUseCase(postRepository: PostRepository): RefreshDataUseCase {
        return RefreshDataUseCase(postRepository)
    }
}