package com.example.petme.ui.addpet

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petme.common.Resource
import com.example.petme.data.model.Post
import com.example.petme.data.model.User
import com.example.petme.domain.usecase.firebaseUseCase.GetCurrentUserUseCase
import com.example.petme.domain.usecase.firebaseUseCase.posts.AddPostUseCase
import com.example.petme.domain.usecase.firebaseUseCase.posts.UploadImagesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PostViewModel @Inject constructor(
    private val uploadImagesUseCase: UploadImagesUseCase,
    private val addPostUseCase: AddPostUseCase,private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _currentUser = MutableStateFlow<Resource<User>?>(Resource.Loading)
    val currentUser: StateFlow<Resource<User>?> = _currentUser


    private val _addPostResult = MutableStateFlow<Resource<Unit>>(Resource.Loading)
    val addPostResult: StateFlow<Resource<Unit>> = _addPostResult






    fun getCurrentUser() {
        viewModelScope.launch {
            try {
                _currentUser.value = getCurrentUserUseCase()
                Log.v("ProfileViewModel", " success get user data ${_currentUser.value.toString()} :")

            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error in getCurrentUser: ${e.message}")
                // Handle the error, show a message, or perform other actions as needed
            }
        }
    }



    fun addPostWithImages(post: Post, imageUris: List<Uri>) {
        viewModelScope.launch {
            // Step 1: Upload images
            _addPostResult.value = Resource.Loading
            val uploadResult = uploadImagesUseCase(imageUris)

            if (uploadResult is Resource.Success) {
                // Step 2: Add post with image URLs
                val addResult = addPostUseCase(post, uploadResult.data)
                _addPostResult.value = addResult
            } else {
                // Image upload failed
                _addPostResult.value = Resource.Error(Exception("Image upload failed"))
            }
        }
    }

}