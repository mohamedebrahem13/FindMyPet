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
import com.example.findmypet.domain.usecase.firebaseUseCase.auth.GetCurrentUserUseCase
import com.example.findmypet.domain.usecase.firebaseUseCase.notification.SendNotificationToTopicUseCase
import com.example.findmypet.domain.usecase.firebaseUseCase.posts.AddPostUseCase
import com.example.findmypet.domain.usecase.firebaseUseCase.posts.GetUserPostCountUseCase
import com.example.findmypet.domain.usecase.firebaseUseCase.posts.UploadImagesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PostViewModel @Inject constructor(
    private val uploadImagesUseCase: UploadImagesUseCase,
    private val addPostUseCase: AddPostUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getUserPostCountUseCase: GetUserPostCountUseCase,
    private val sendNotificationToTopicUseCase: SendNotificationToTopicUseCase
) : ViewModel() {

    private val _currentUser = MutableStateFlow<Resource<User>?>(Resource.Loading)
    val currentUser: StateFlow<Resource<User>?> = _currentUser

    private val _postCount = MutableSharedFlow<Int>()
    val postCount: SharedFlow<Int> = _postCount.asSharedFlow()

    private val _addPostResult = MutableStateFlow<Resource<Unit>>(Resource.Loading)
    val addPostResult: StateFlow<Resource<Unit>> = _addPostResult

    private val _selectedImageUrisFlow = MutableStateFlow<List<Uri>>(emptyList())
    val selectedImageUrisFlow: StateFlow<List<Uri>> = _selectedImageUrisFlow

    init {
        getUserPostCount()
    }

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
                Log.v("addPetViewModel", " success get user data ${_currentUser.value.toString()} :")

            } catch (e: Exception) {
                Log.e("addPetViewModel", "Error in getCurrentUser: ${e.message}")
            }
        }
    }

    private fun getUserPostCount() {
        viewModelScope.launch {
            try {
                val count = getUserPostCountUseCase()
                if (count != null) {
                    _postCount.emit(count)
                }
            } catch (e: Exception) {
                // Handle error
                Log.e("YourFragmentViewModel", "Error getting user post count: ${e.message}")
            }
        }
    }


    fun addPostWithImages(post: Post, imageUris: List<Uri>) {
        viewModelScope.launch {
            // Step 1: Upload images
            _addPostResult.value = Resource.Loading
            val uploadResult = uploadImagesUseCase(imageUris)

            if (uploadResult is Resource.Success) {
                // Assuming uploadResult.data contains list of image download URLs
                val imageUrls = uploadResult.data

                // Step 2: Add post with image URLs
                val addResult = addPostUseCase(post, imageUrls)

                // Send notification with the first image URL
                val firstImageUrl = imageUrls.firstOrNull() // Assuming imageUrls is not empty
                val notificationBody = "$ADDITIONAL_TEXT ${post.pet_name}"

                // Ensure firstImageUrl is not null before sending the notification
                if (firstImageUrl != null) {
                    delay(30000) // 30 seconds delay (adjust as needed)
                    sendNotificationToTopicUseCase.sendNotificationToTopic(
                        "New Pet Notification", notificationBody,
                        Constant.Topic, firstImageUrl // Include the first image URL in the payload
                    )
                } else {
                    // Handle case where no images were uploaded
                    _addPostResult.value = Resource.Error(Throwable("No images uploaded"))
                }

                // Update add post result state
                _addPostResult.value = addResult

            } else {
                // Image upload failed
                _addPostResult.value = Resource.Error(Throwable("Image upload failed"))
            }
        }
    }

}