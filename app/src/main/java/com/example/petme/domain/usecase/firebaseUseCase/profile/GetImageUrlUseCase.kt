package com.example.petme.domain.usecase.firebaseUseCase.profile

import com.example.petme.domain.repository.GetImageUrlFromFirestoreResponse
import com.example.petme.domain.repository.EditProfileRepository
import javax.inject.Inject

class GetImageUrlUseCase  @Inject constructor(private val EditProfileRepository: EditProfileRepository){

    suspend operator fun invoke():GetImageUrlFromFirestoreResponse=EditProfileRepository.getImageUrlFromFirestore()
}