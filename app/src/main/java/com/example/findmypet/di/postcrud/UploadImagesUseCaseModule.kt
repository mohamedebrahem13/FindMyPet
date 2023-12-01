package com.example.findmypet.di.postcrud

import com.example.findmypet.domain.repository.PostRepository
import com.example.findmypet.domain.usecase.firebaseUseCase.posts.UploadImagesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object UploadImagesUseCaseModule {
    @Provides
    @ViewModelScoped
    fun provideUploadImagesUseCase(postRepository: PostRepository): UploadImagesUseCase {
        return UploadImagesUseCase(postRepository)
    }
}
