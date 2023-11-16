package com.example.findmypet.ui.allposts

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findmypet.common.Resource
import com.example.findmypet.data.model.Post
import com.example.findmypet.data.model.User
import com.example.findmypet.domain.usecase.firebaseUseCase.GetCurrentUserUseCase
import com.example.findmypet.domain.usecase.firebaseUseCase.posts.GetPostsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllPostsViewModel @Inject constructor(private val getPostsUseCase: GetPostsUseCase, private val getCurrentUserUseCase: GetCurrentUserUseCase): ViewModel(){

    private val _sortedPosts = MutableStateFlow<Resource<List<Post>>>(Resource.Loading)
    val sortedPosts: StateFlow<Resource<List<Post>>> = _sortedPosts

    private val _currentUser = MutableStateFlow<Resource<User>?>(Resource.Loading)
    val currentUser: StateFlow<Resource<User>?> = _currentUser

    // StateFlow for search results
    private val _searchedPosts = MutableStateFlow<Resource<List<Post>>>(Resource.Loading)
    val searchedPosts: StateFlow<Resource<List<Post>>> = _searchedPosts

    // Variable to store the last full list of posts
    private var lastFullList: List<Post>? = null



    fun getPosts(){
        viewModelScope.launch {
            try {
                // Get all posts
                val result = getPostsUseCase.execute()

                if (result is Resource.Success) {
                    // Store the full list
                    lastFullList = result.data
                    _sortedPosts.value = Resource.Success(lastFullList!!)
                } else {
                    // Handle error from getPostsUseCase
                    _sortedPosts.value = result
                }
            } catch (e: Exception) {
                _sortedPosts.value = Resource.Error(e)
            }
        }

    }


    // Function to perform user search by pet_name
    fun searchPostsByPetName(petNameQuery: String?) {
        viewModelScope.launch {
            try {
                // If the search query is empty or null, return the full list of posts
                if (petNameQuery.isNullOrBlank()) {
                    // Check if we have a stored full list, use it if available
                    val fullList = lastFullList ?: emptyList()
                    _searchedPosts.value = Resource.Success(fullList)
                } else {
                    // Filter posts based on the pet_name field
                    val filteredPosts = lastFullList?.filter { post ->
                        post.pet_name.contains(petNameQuery, ignoreCase = true)
                    } ?: emptyList()
                    _searchedPosts.value = Resource.Success(filteredPosts)
                }
            } catch (e: Exception) {
                // Handle other exceptions
                _searchedPosts.value = Resource.Error(e)
            }
        }
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
}
