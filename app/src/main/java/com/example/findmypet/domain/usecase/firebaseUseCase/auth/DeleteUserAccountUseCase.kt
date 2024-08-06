package com.example.findmypet.domain.usecase.firebaseUseCase.auth

import com.example.findmypet.common.Resource
import com.example.findmypet.domain.repository.Authenticator
import javax.inject.Inject

class DeleteUserAccountUseCase @Inject constructor(
    private val authenticator: Authenticator
) {
    suspend operator fun invoke(): Resource<Unit> {
        return try {
            authenticator.deleteUserAccount()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}