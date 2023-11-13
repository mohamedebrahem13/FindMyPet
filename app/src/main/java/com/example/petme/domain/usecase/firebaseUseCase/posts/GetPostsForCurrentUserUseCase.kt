package com.example.petme.domain.usecase.firebaseUseCase.posts

import com.example.petme.common.Resource
import com.example.petme.data.model.Post
import com.example.petme.domain.repository.PostRepository
import javax.inject.Inject

class GetPostsForCurrentUserUseCase @Inject constructor(private val postRepository: PostRepository) {
    suspend fun execute(): Resource<List<Post>> {
        return postRepository.getPostsForUserById()
    }
}
