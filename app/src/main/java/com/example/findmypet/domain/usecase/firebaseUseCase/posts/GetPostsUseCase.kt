package com.example.findmypet.domain.usecase.firebaseUseCase.posts

import com.example.findmypet.common.Resource
import com.example.findmypet.data.model.Post
import com.example.findmypet.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPostsUseCase @Inject constructor(private val postRepository: PostRepository) {
    suspend fun execute(): Flow<Resource<List<Post>>> {
        return postRepository.refreshPosts()
    }
}