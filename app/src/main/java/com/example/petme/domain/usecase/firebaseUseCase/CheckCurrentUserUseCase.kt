package com.example.petme.domain.usecase.firebaseUseCase

import com.example.petme.domain.repository.Authenticator
import javax.inject.Inject

class CheckCurrentUserUseCase @Inject constructor(
    private val authenticator: Authenticator
) {
    suspend operator fun invoke(): Boolean = authenticator.isCurrentUserExist()
}