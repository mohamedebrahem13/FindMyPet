package com.example.findmypet.domain.usecase.firebaseUseCase.posts

import com.example.findmypet.common.Resource
import com.example.findmypet.data.model.Post
import com.example.findmypet.domain.repository.PostRepository
import javax.inject.Inject

class AddPostUseCase @Inject constructor(private val postRepository: PostRepository) {
    suspend operator fun invoke(post: Post, imageUrls: List<String>): Resource<Unit> {
        post.imageUrls = imageUrls
        return try {
            val addResult = postRepository.addPostRemote(post)
            when (addResult) {
                is Resource.Success -> Resource.Success(Unit)
                is Resource.Error -> addResult
                is Resource.Loading -> Resource.Loading
            }
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}