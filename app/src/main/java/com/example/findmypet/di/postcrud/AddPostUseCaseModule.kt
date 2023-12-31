package com.example.findmypet.di.postcrud

import com.example.findmypet.domain.repository.PostRepository
import com.example.findmypet.domain.usecase.firebaseUseCase.posts.AddPostUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object AddPostUseCaseModule {
    @Provides
    @ViewModelScoped
    fun provideAddPostUseCase(postRepository: PostRepository): AddPostUseCase {
        return AddPostUseCase(postRepository)
    }
}
