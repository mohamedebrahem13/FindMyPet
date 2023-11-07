package com.example.petme.domain.usecase.firebaseUseCase.profile

import com.example.petme.common.UpdateUserProfileResponse
import com.example.petme.data.model.User
import com.example.petme.domain.repository.EditProfileRepository
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(private val EditProfileRepository: EditProfileRepository){

    suspend operator fun invoke(User:User): UpdateUserProfileResponse =EditProfileRepository.updateUserProfile(User)
}