package com.example.findmypet.di.auth

import com.example.findmypet.domain.repository.Authenticator
import com.example.findmypet.domain.usecase.firebaseUseCase.CheckCurrentUserUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object CheckCurrentUserUseCaseModule {
    @Provides
    @ViewModelScoped
    fun provideCheckCurrentUserUseCase(authenticator: Authenticator): CheckCurrentUserUseCase {
        return CheckCurrentUserUseCase(authenticator)
    }
}
