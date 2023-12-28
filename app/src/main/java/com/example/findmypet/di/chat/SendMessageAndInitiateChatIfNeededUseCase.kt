package com.example.findmypet.di.chat

import com.example.findmypet.domain.repository.ChatRepositoryInterface
import com.example.findmypet.domain.usecase.firebaseUseCase.chat.SendMessageAndInitiateChatIfNeededUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped


@Module
@InstallIn(ViewModelComponent::class)
object SendMessageAndInitiateChatIfNeededUseCase {


    @Provides
    @ViewModelScoped
    fun provideSendMessageAndInitiateChatIfNeededUseCase(chatRepository: ChatRepositoryInterface): SendMessageAndInitiateChatIfNeededUseCase {
        return SendMessageAndInitiateChatIfNeededUseCase(chatRepository)
    }
}