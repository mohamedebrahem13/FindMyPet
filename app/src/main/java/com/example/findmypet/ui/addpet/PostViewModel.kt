package com.example.findmypet.ui.addpet

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findmypet.common.Constant
import com.example.findmypet.common.Constant.ADDITIONAL_TEXT
import com.example.findmypet.common.Resource
import com.example.findmypet.data.model.Post
import com.example.findmypet.data.model.User
import com.example.findmypet.domain.usecase.firebaseUseCase.GetCurrentUserUseCase
import com.example.findmypet.domain.usecase.firebaseUseCase.notification.SendNotificationToTopicUseCase
import com.example.findmypet.domain.usecase.firebaseUseCase.posts.AddPostUseCase
import com.example.findmypet.domain.usecase.firebaseUseCase.posts.UploadImagesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PostViewModel @Inject constructor(
    private val uploadImagesUseCase: UploadImagesUseCase,
    private val addPostUseCase: AddPostUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
    ,private val sendNotificationToTopicUseCase: SendNotificationToTopicUseCase
) : ViewModel() {

    private val _currentUser = MutableStateFlow<Resource<User>?>(Resource.Loading)
    val currentUser: StateFlow<Resource<User>?> = _currentUser


    private val _addPostResult = MutableStateFlow<Resource<Unit>>(Resource.Loading)
    val addPostResult: StateFlow<Resource<Unit>> = _addPostResult

    private val _selectedImageUrisFlow = MutableStateFlow<List<Uri>>(emptyList())
    val selectedImageUrisFlow: StateFlow<List<Uri>> = _selectedImageUrisFlow


    fun updateSelectedImageUris(uris: List<Uri>) {
        _selectedImageUrisFlow.value = uris
    }

    fun removeImageUriAtPosition(position: Int) {
        val currentUris = _selectedImageUrisFlow.value.toMutableList()
        currentUris.removeAt(position)
        _selectedImageUrisFlow.value = currentUris
    }



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


                val notificationBody = "$ADDITIONAL_TEXT ${post.pet_name}"

                sendNotificationToTopicUseCase. sendNotificationToTopic(
                    "New Pet Notification", notificationBody,
                    Constant.Topic
                )

                //here notify the users
                _addPostResult.value = addResult

            } else {
                // Image upload failed
                _addPostResult.value = Resource.Error(Throwable("Image upload failed"))
            }
        }
    }

}