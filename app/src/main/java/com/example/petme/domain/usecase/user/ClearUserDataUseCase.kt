package com.example.petme.domain.usecase.user

import com.example.petme.domain.repository.PostRepository
import javax.inject.Inject

class ClearUserDataUseCase @Inject constructor(private val postRepository: PostRepository) {

    suspend fun execute() {
        postRepository.clearUserData()
        // Add additional clearing logic for other repositories if needed
    }
}