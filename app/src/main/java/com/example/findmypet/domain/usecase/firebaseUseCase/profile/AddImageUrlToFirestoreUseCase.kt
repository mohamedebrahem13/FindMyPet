package com.example.findmypet.domain.usecase.firebaseUseCase.profile

import android.net.Uri
import com.example.findmypet.domain.repository.AddImageUrlToFirestoreResponse
import com.example.findmypet.domain.repository.EditProfileRepository
import javax.inject.Inject

class AddImageUrlToFirestoreUseCase @Inject constructor(private val EditProfileRepository:EditProfileRepository ) {
    suspend operator fun invoke (downloadUri:Uri):AddImageUrlToFirestoreResponse = EditProfileRepository.addImageUrlToFirestore(downloadUri)

}