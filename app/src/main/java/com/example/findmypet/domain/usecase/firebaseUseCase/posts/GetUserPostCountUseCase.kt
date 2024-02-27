package com.example.findmypet.domain.usecase.firebaseUseCase.posts


import com.example.findmypet.domain.repository.PostRepository
import javax.inject.Inject

class GetUserPostCountUseCase @Inject constructor(private val postRepository: PostRepository) {
    suspend operator fun invoke():  Int? {
        return postRepository.getUserPostCount()
    }
}
