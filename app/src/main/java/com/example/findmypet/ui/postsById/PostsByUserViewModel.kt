package com.example.findmypet.ui.postsById

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findmypet.common.Resource
import com.example.findmypet.data.model.Post
import com.example.findmypet.domain.usecase.firebaseUseCase.posts.DeletePostUseCase
import com.example.findmypet.domain.usecase.firebaseUseCase.posts.GetPostsForCurrentUserUseCase
import com.google.firebase.firestore.FirebaseFirestoreException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PostsByUserViewModel @Inject constructor(
    private val getPostsForCurrentUserUseCase: GetPostsForCurrentUserUseCase, private val deletePostUseCase: DeletePostUseCase
) : ViewModel() {

    private val _userPostsStateFlow = MutableStateFlow<Resource<List<Post>>>(Resource.Loading)
    val userPostsStateFlow: StateFlow<Resource<List<Post>>> = _userPostsStateFlow

    private val _deletePostStateFlow = MutableSharedFlow<Resource<Unit>>()
    val deletePostSharedFlow: SharedFlow<Resource<Unit>> = _deletePostStateFlow.asSharedFlow()


    init {
        fetchPostsForCurrentUser()
    }

    fun observeDeletePostAction(postId: String) {
        viewModelScope.launch {
            try {
                _deletePostStateFlow.emit(Resource.Loading)
                val result = deletePostUseCase.execute(postId)
                _deletePostStateFlow.emit(result)
            } catch (e: Exception) {
                if (e is FirebaseFirestoreException && e.code == FirebaseFirestoreException.Code.UNAVAILABLE) {
                    _deletePostStateFlow.emit(Resource.Error(e))
                } else {
                    _deletePostStateFlow.emit(Resource.Error(e))
                }
            }

        }
    }



     fun fetchPostsForCurrentUser() {
        viewModelScope.launch {
            try {
                getPostsForCurrentUserUseCase().collect { postsResource ->
                    _userPostsStateFlow.value = postsResource
                }
            } catch (e: Throwable) {
                _userPostsStateFlow.value = Resource.Error(e)
                // e.g., handle network or other errors
            }
        }
    }

}