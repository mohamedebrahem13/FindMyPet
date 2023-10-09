package com.example.petme.domain.usecase.firebaseUseCase

import com.example.petme.common.Resource
import com.example.petme.data.model.User
import com.example.petme.domain.repository.Authenticator
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authenticator: Authenticator
) {
    suspend operator fun invoke(
        user: User,
        password: String
    ): Resource<Unit> {
        return try {
            Resource.Loading
            Resource.Success(authenticator.signUpWithEmailAndPassword(user, password))
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}