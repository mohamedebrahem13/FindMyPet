package com.example.findmypet.domain.usecase.firebaseUseCase.posts

import com.example.findmypet.common.Resource
import com.example.findmypet.domain.repository.PostRepository
import javax.inject.Inject

class RemovePostFromFavoriteUseCase@Inject constructor(private val postRepository: PostRepository) {
    suspend fun execute(postId: String): Resource<Unit> {
        return postRepository.removePostFromFavorite(postId)
    }
}