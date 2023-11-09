package com.example.petme.di

import com.example.petme.domain.repository.PostRepository
import com.example.petme.domain.usecase.firebaseUseCase.posts.GetPostsForCurrentUserUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object GetPostsForCurrentUserUseCaseModule {
    @Provides
    @ViewModelScoped
    fun provideGetPostsForCurrentUserUseCase(postRepository: PostRepository): GetPostsForCurrentUserUseCase {
        return GetPostsForCurrentUserUseCase(postRepository)
    }
}
