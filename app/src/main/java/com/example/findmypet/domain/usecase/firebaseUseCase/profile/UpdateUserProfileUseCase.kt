package com.example.findmypet.domain.usecase.firebaseUseCase.profile

import com.example.findmypet.common.UpdateUserProfileResponse
import com.example.findmypet.data.model.User
import com.example.findmypet.domain.repository.EditProfileRepository
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(private val EditProfileRepository: EditProfileRepository){

    suspend operator fun invoke(User:User): UpdateUserProfileResponse =EditProfileRepository.updateUserProfile(User)
}