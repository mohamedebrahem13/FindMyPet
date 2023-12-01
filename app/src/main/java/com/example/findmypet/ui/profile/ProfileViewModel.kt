package com.example.findmypet.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findmypet.common.Resource
import com.example.findmypet.data.model.User
import com.example.findmypet.domain.usecase.firebaseUseCase.GetCurrentUserUseCase
import com.example.findmypet.domain.usecase.firebaseUseCase.SignOutUseCase
import com.example.findmypet.domain.usecase.firebaseUseCase.profile.GetImageUrlUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val getImageUrlUseCase:GetImageUrlUseCase
) : ViewModel() {

    private val _currentUser = MutableStateFlow<Resource<User>?>(Resource.Loading)
    val currentUser: StateFlow<Resource<User>?> = _currentUser


    private val _getImageUrl = MutableStateFlow<Resource<String>>(Resource.Loading)
    val getImageUrl: StateFlow<Resource<String>> = _getImageUrl







    private fun getImageUrl() {
        viewModelScope.launch {
            try {
                val result = getImageUrlUseCase()
                _getImageUrl.value = result
                Log.e("ProfileViewModel", "sucsses in getImageUrl in ProfileViewModel: $result")

            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error in getImageUrl in ProfileViewModel: ${e.message}")
                // Handle the error, show a message, or perform other actions as needed
            }
        }
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


    fun signOut() {
        viewModelScope.launch {
            try {
                // Sign out the user
                signOutUseCase()

                Log.v("ProfileViewModel", "User is signed out successfully")
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error in signOut: ${e.message}")
                // Handle the error, show a message, or perform other actions as needed
            }
        }
    }
}
