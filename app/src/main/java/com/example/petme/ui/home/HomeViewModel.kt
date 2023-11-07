package com.example.petme.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petme.common.Resource
import com.example.petme.data.model.Post
import com.example.petme.data.model.User
import com.example.petme.domain.usecase.firebaseUseCase.GetCurrentUserUseCase
import com.example.petme.domain.usecase.firebaseUseCase.posts.GetPostsSortedByTimestampUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(private val getCurrentUserUseCase: GetCurrentUserUseCase,private val getPostsSortedByTimestampUseCase: GetPostsSortedByTimestampUseCase) : ViewModel() {

    private val _sortedPosts = MutableStateFlow<Resource<List<Post>>>(Resource.Loading)
    val sortedPosts: StateFlow<Resource<List<Post>>> = _sortedPosts

    private val _currentUser = MutableStateFlow<Resource<User>?>(Resource.Loading)
    val currentUser: StateFlow<Resource<User>?> = _currentUser


    init {
        viewModelScope.launch {
            try {
                val result = getPostsSortedByTimestampUseCase.execute()
                _sortedPosts.value = result
            } catch (e: Exception) {
                _sortedPosts.value = Resource.Error(e)
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
