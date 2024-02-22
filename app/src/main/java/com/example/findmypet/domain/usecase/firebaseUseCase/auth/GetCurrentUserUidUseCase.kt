package com.example.findmypet.domain.usecase.firebaseUseCase.auth

import com.example.findmypet.domain.repository.Authenticator
import javax.inject.Inject

class GetCurrentUserUidUseCase @Inject constructor(
    private val authenticator: Authenticator
) {
    suspend operator fun invoke(): String = authenticator.getFirebaseUserUid()
}