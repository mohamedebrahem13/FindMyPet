package com.example.petme.domain.usecase.firebaseUseCase.profile

import android.net.Uri
import com.example.petme.domain.repository.AddImageToStorageResponse
import com.example.petme.domain.repository.EditProfileRepository
import javax.inject.Inject

class AddImageToFirebaseStorageUseCase @Inject constructor(private val EditProfileRepository: EditProfileRepository) {

    suspend operator fun invoke (imageUri: Uri):AddImageToStorageResponse =EditProfileRepository.addImageToFirebaseStorage(imageUri)
}