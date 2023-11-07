package com.example.petme.domain.usecase.firebaseUseCase

import com.example.petme.domain.repository.Authenticator
import javax.inject.Inject

class GetCurrentUserUidUseCase @Inject constructor(
    private val authenticator: Authenticator
) {
    suspend operator fun invoke(): String = authenticator.getFirebaseUserUid()
}