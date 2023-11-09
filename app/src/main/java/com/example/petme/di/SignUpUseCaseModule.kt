package com.example.petme.di

import com.example.petme.domain.repository.Authenticator
import com.example.petme.domain.usecase.firebaseUseCase.SignUpUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object SignUpUseCaseModule {
    @Provides
    @ViewModelScoped
    fun provideSignUpUseCase(authenticator: Authenticator): SignUpUseCase {
        return SignUpUseCase(authenticator)
    }
}
