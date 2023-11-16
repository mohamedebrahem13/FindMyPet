package com.example.findmypet.ui.postsById

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findmypet.common.Resource
import com.example.findmypet.data.model.Post
import com.example.findmypet.domain.usecase.firebaseUseCase.posts.DeletePostUseCase
import com.example.findmypet.domain.usecase.firebaseUseCase.posts.GetPostsForCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PostsByUserViewModel @Inject constructor(
    private val getPostsForCurrentUserUseCase: GetPostsForCurrentUserUseCase, private val deletePostUseCase: DeletePostUseCase
) : ViewModel() {

    private val _postsStateFlow  = MutableStateFlow<Resource<List<Post>>>(Resource.Loading)
    val postsStateFlow: StateFlow<Resource<List<Post>>> = _postsStateFlow

    private val _deletePostStateFlow = MutableStateFlow<Resource<Unit>>(Resource.Success(Unit))
    val deletePostStateFlow: StateFlow<Resource<Unit>> = _deletePostStateFlow


    fun deletePost(postId: String) {
        viewModelScope.launch {
            try {
                _deletePostStateFlow.value = Resource.Loading
                val result = deletePostUseCase(postId)
                _deletePostStateFlow.value = result
                // Handle the result as needed (update UI, show success message, etc.)
            } catch (e: Exception) {
                _deletePostStateFlow.value = Resource.Error(e)
            }
        }
    }



    fun getPostsForCurrentUser() {
        viewModelScope.launch {
            val result = getPostsForCurrentUserUseCase.execute()
            _postsStateFlow.value = result
        }
    }
}