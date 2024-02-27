package com.example.findmypet.domain.usecase.firebaseUseCase.posts

import com.example.findmypet.common.Resource
import com.example.findmypet.data.model.Post
import com.example.findmypet.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchPostsByNameUseCase @Inject constructor(private val postRepository: PostRepository) {
    operator fun invoke(petName: String): Flow<Resource<List<Post>>> {
        return postRepository.searchPostsByPetName(petName)
    }
}