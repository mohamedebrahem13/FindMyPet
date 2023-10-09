package com.example.petme.domain.usecase.firebaseUseCase

import com.example.petme.common.Resource
import com.example.petme.data.model.User
import com.example.petme.domain.repository.Authenticator
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authenticator: Authenticator
) {
    suspend operator fun invoke(): Resource<User> {

        return try {
            Resource.Loading
            Resource.Success(authenticator.getCurrentUser())
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}