package com.example.findmypet.domain.usecase.firebaseUseCase.posts

import com.example.findmypet.common.Resource
import com.example.findmypet.data.model.Post
import com.example.findmypet.domain.repository.PostRepository
import javax.inject.Inject

class GetPostsForCurrentUserUseCase @Inject constructor(private val postRepository: PostRepository) {
    suspend fun execute(): Resource<List<Post>> {
        return postRepository.getPostsForUserById()
    }
}
