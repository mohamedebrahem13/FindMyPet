package com.example.findmypet.ui.profile.edit

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findmypet.common.Resource
import com.example.findmypet.common.UpdateUserProfileResponse
import com.example.findmypet.data.model.User
import com.example.findmypet.domain.usecase.firebaseUseCase.auth.GetCurrentUserUidUseCase
import com.example.findmypet.domain.usecase.firebaseUseCase.profile.AddImageToFirebaseStorageUseCase
import com.example.findmypet.domain.usecase.firebaseUseCase.profile.AddImageUrlToFirestoreUseCase
import com.example.findmypet.domain.usecase.firebaseUseCase.profile.UpdateUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProfileEditViewModel  @Inject constructor(private val userProfileUseCase:UpdateUserProfileUseCase, private val addImageUrlToFirestoreUseCase: AddImageUrlToFirestoreUseCase, private val addImageToFirebaseStorageUseCase: AddImageToFirebaseStorageUseCase, private val getCurrentUserUidUseCase: GetCurrentUserUidUseCase): ViewModel() {


    private val _updateProfileResult = MutableStateFlow<UpdateUserProfileResponse>(UpdateUserProfileResponse.Loading)
    val updateProfileResult: StateFlow<UpdateUserProfileResponse> = _updateProfileResult


    // State flow for tracking the overall progress
    private val _progress = MutableStateFlow<Resource<String>?>(Resource.Loading)
    val progress: StateFlow<Resource<String>?> = _progress

    private var uid: String = ""



    init {
        setUid()
    }


    private fun addImage(uri: Uri, newUser: User) {
        viewModelScope.launch {
            try {
                // First, upload the image to Firebase Storage
                _progress.value = Resource.Loading
                when (val storageResult = addImageToFirebaseStorageUseCase(uri)) {
                    is Resource.Success -> {
                        // Now that storage is successful, upload the image URL to Firebase Firestore
                        val imageUrlResult = addImageUrlToFirestoreUseCase(storageResult.data)
                        when (imageUrlResult) {
                            is Resource.Success -> {
                                // Image upload and URL update both successful
                                _progress.value = Resource.Success("Image uploaded successfully.")

                                // Now, update the user profile with the new image URL
                                val updatedUser = newUser.copy(imagePath = imageUrlResult.data)
                                _progress.value = Resource.Loading
                                val updateResult = userProfileUseCase(updatedUser)
                                _updateProfileResult.value = updateResult
                            }
                            is Resource.Error -> {
                                // Handle error for adding image URL
                                _progress.value = Resource.Error(Throwable("Error adding image URL"))
                            }
                            else -> {}
                        }
                    }
                    is Resource.Error -> {
                        // Handle error for adding image to storage
                        _progress.value = Resource.Error(Throwable("Error uploading image to storage"))
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                _progress.value = Resource.Error(e)
            }
        }
    }

    fun updateProfile(imageUri: Uri?, newUser: User) {
        viewModelScope.launch {
            if (imageUri == null) {
                // Update the user profile without adding an image
                _progress.value = Resource.Loading
                val updateResult = userProfileUseCase(newUser)
                _updateProfileResult.value = updateResult
            } else {
                addImage(imageUri, newUser)
            }
        }
    }



    private fun setUid() {
        viewModelScope.launch {
            try {
                uid = getCurrentUserUidUseCase()
                Log.v("uid", "user uid$uid ")
            } catch (e: Exception) {
                Log.e(
                    "EditProfileViewModel",
                    "Error in getCurrentUserUidUseCase in EditProfileViewModel: ${e.message}"
                )
            }

        }


    }
}