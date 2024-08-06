package com.example.findmypet.domain.usecase.firebaseUseCase.posts

import com.example.findmypet.common.Resource
import com.example.findmypet.domain.repository.PostRepository
import javax.inject.Inject

class DeleteUserPostsUseCase @Inject constructor(private val postRepository: PostRepository){
    suspend fun execute(): Resource<Unit> {
        return try {
            postRepository.deleteUserPosts()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

}