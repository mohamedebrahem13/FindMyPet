package com.example.findmypet.domain.usecase.firebaseUseCase.profile

import com.example.findmypet.domain.repository.GetImageUrlFromFirestoreResponse
import com.example.findmypet.domain.repository.EditProfileRepository
import javax.inject.Inject

class GetImageUrlUseCase  @Inject constructor(private val EditProfileRepository: EditProfileRepository){

    suspend operator fun invoke():GetImageUrlFromFirestoreResponse=EditProfileRepository.getImageUrlFromFirestore()
}