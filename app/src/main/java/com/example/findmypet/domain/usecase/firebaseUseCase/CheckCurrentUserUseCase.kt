package com.example.findmypet.domain.usecase.firebaseUseCase

import com.example.findmypet.domain.repository.Authenticator
import javax.inject.Inject

class CheckCurrentUserUseCase @Inject constructor(
    private val authenticator: Authenticator
) {
    suspend operator fun invoke(): Boolean = authenticator.isCurrentUserExist()
}