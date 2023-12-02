package com.example.findmypet.ui.allposts

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findmypet.common.Resource
import com.example.findmypet.data.model.Post
import com.example.findmypet.data.model.User
import com.example.findmypet.domain.usecase.firebaseUseCase.GetCurrentUserUseCase
import com.example.findmypet.domain.usecase.firebaseUseCase.posts.AddPostToFavoriteUseCase
import com.example.findmypet.domain.usecase.firebaseUseCase.posts.GetPostsUseCase
import com.example.findmypet.domain.usecase.firebaseUseCase.posts.RemovePostFromFavoriteUseCase
import com.google.firebase.firestore.FirebaseFirestoreException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllPostsViewModel @Inject constructor(private val getPostsUseCase: GetPostsUseCase, private val getCurrentUserUseCase: GetCurrentUserUseCase,private val AddPostToFavoriteUseCase:AddPostToFavoriteUseCase ,private val removePostFromFavoriteUseCase: RemovePostFromFavoriteUseCase): ViewModel(){


    private var originalPosts: List<Post> = emptyList()

    private val _postsStateFlow = MutableStateFlow<Resource<List<Post>>>(Resource.Loading)
    val postsStateFlow: StateFlow<Resource<List<Post>>> = _postsStateFlow

    private val _currentUser = MutableStateFlow<Resource<User>?>(Resource.Loading)
    val currentUser: StateFlow<Resource<User>?> = _currentUser

    private val _addFaveSharedFlow = MutableSharedFlow<Resource<Unit>>()
    val addFaveSharedFlow: SharedFlow<Resource<Unit>> = _addFaveSharedFlow.asSharedFlow()

    private val _removeFaveSharedFlow = MutableSharedFlow<Resource<Unit>>()
    val removeFaveSharedFlow: SharedFlow<Resource<Unit>> = _removeFaveSharedFlow.asSharedFlow()



    init {
        fetchPosts()
    }


    fun fetchPosts() {
        viewModelScope.launch {
            try {
                val result = getPostsUseCase.execute()
                result.collect { postsResource ->
                    if (postsResource is Resource.Success) {
                        originalPosts = postsResource.data // Store original posts
                    }
                    _postsStateFlow.value = postsResource // Update StateFlow
                }
            } catch (e: Throwable) {
                _postsStateFlow.value = Resource.Error(e)
                // Handle error state as needed
            }
        }
    }

    // Method to perform search
    fun searchPosts(query: String) {
        val searchedPosts = originalPosts.filter { post ->
            post.pet_name.contains(query, ignoreCase = true)
        }
        _postsStateFlow.value = Resource.Success(searchedPosts)
    }


    // Method to revert to the original list
    fun resetSearch() {
        _postsStateFlow.value = Resource.Success(originalPosts)
    }



    fun getCurrentUser() {
        viewModelScope.launch {
            try {
                _currentUser.value = getCurrentUserUseCase()
            } catch (e: Exception) {
                Log.e("homeViewModel", "Error in getCurrentUser IN HomeViewModel: ${e.message}")
                // Handle the error, show a message, or perform other actions as needed
            }
        }
    }


    fun removeFav(postId: String) {
        viewModelScope.launch {
            try {
                _removeFaveSharedFlow.emit(Resource.Loading)
                val result = removePostFromFavoriteUseCase.execute(postId)
                _removeFaveSharedFlow.emit(result)
            } catch (e: Exception) {
                if (e is FirebaseFirestoreException && e.code == FirebaseFirestoreException.Code.UNAVAILABLE) {
                    _removeFaveSharedFlow.emit(Resource.Error(e))
                } else {
                    _removeFaveSharedFlow.emit(Resource.Error(e))
                }
            }
        }
    }

    fun addFav(postId:String) {
        viewModelScope.launch {
            try {
                _addFaveSharedFlow.emit(Resource.Loading)
                val result = AddPostToFavoriteUseCase.execute(postId)
                _addFaveSharedFlow.emit(result)
            } catch (e: Exception) {
                if (e is FirebaseFirestoreException && e.code == FirebaseFirestoreException.Code.UNAVAILABLE) {
                    _addFaveSharedFlow.emit(Resource.Error(e))
                } else {
                    _addFaveSharedFlow.emit(Resource.Error(e))
                }
            }

        }
    }


}
