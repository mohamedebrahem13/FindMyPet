package com.example.petme.domain.usecase.firebaseUseCase.worker

import com.example.petme.domain.repository.PostRepository
import javax.inject.Inject

class RefreshDataUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend fun execute() {
        postRepository.refreshPosts()

    }
}
