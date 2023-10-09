package com.example.petme.domain.usecase.firebaseUseCase

import com.example.petme.common.Resource
import com.example.petme.domain.repository.Authenticator
import javax.inject.Inject

class ForgotPasswordUseCase @Inject constructor(
    private val authenticator: Authenticator
) {
    suspend operator fun invoke(email: String): Resource<Void> {

        return try {
            Resource.Loading
            Resource.Success(authenticator.sendPasswordResetEmail(email))
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}