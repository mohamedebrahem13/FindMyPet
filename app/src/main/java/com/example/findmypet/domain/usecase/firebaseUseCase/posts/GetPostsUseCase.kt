package com.example.findmypet.domain.usecase.firebaseUseCase.posts

import com.example.findmypet.common.Resource
import com.example.findmypet.data.model.Post
import com.example.findmypet.domain.repository.PostRepository
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetPostsUseCase @Inject constructor(private val postRepository: PostRepository) {

    private var isLoading = false
    private var isLastPage = false // Shared variable to track last page state

    fun execute(lastVisible: DocumentSnapshot? = null): Flow<Pair<Resource<List<Post>>, DocumentSnapshot?>> = flow {
        if (isLoading || isLastPage) return@flow // Prevent unnecessary calls

        isLoading = true

        try {
            val result = postRepository.refreshPosts(lastVisible) // Pass lastVisible
            result.collect { (resource, newLastVisible) ->
                when (resource) {
                    is Resource.Success -> {
                        val newPosts = resource.data
                        if (newPosts.isEmpty()) {
                            // Emit an error indicating no more data to load
                            emit(Resource.Error(Exception("No more data to load")) to null)
                        } else {
                            // Emit resource and lastVisible
                            emit(resource to newLastVisible)

                            isLoading = false
                            isLastPage = newPosts.isEmpty() // Set isLastPage based on emptiness
                        }
                    }
                    is Resource.Error -> {
                        isLoading = false
                        emit(resource to null)
                    }
                    is Resource.Loading -> {
                        emit(resource to null)                    }
                }
            }
        } catch (e: Throwable) {
            isLoading = false
            emit(Resource.Error(e) to null)
        }
    }
}


