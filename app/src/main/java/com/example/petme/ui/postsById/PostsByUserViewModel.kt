package com.example.petme.ui.postsById

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petme.common.Resource
import com.example.petme.data.model.Post
import com.example.petme.domain.usecase.firebaseUseCase.posts.GetPostsForCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PostsByUserViewModel @Inject constructor(
    private val getPostsForCurrentUserUseCase: GetPostsForCurrentUserUseCase
) : ViewModel() {

    private val _postsStateFlow  = MutableStateFlow<Resource<List<Post>>>(Resource.Loading)
    val postsStateFlow: StateFlow<Resource<List<Post>>> = _postsStateFlow

    fun getPostsForCurrentUser() {
        viewModelScope.launch {
            val result = getPostsForCurrentUserUseCase.execute()
            _postsStateFlow.value = result
        }
    }
}