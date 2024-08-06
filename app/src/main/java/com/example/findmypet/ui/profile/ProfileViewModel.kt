package com.example.findmypet.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findmypet.common.Resource
import com.example.findmypet.data.model.User
import com.example.findmypet.domain.usecase.firebaseUseCase.auth.DeleteUserAccountUseCase
import com.example.findmypet.domain.usecase.firebaseUseCase.auth.GetCurrentUserUseCase
import com.example.findmypet.domain.usecase.firebaseUseCase.auth.SignOutUseCase
import com.example.findmypet.domain.usecase.firebaseUseCase.posts.DeleteUserPostsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val deleteUserAccountUseCase: DeleteUserAccountUseCase,
    private val deleteUserPostsUseCase: DeleteUserPostsUseCase
) : ViewModel() {

    private val _currentUser = MutableStateFlow<Resource<User>?>(Resource.Loading)
    val currentUser: StateFlow<Resource<User>?> = _currentUser
    private val _deleteAccountStatus = MutableStateFlow<Resource<Unit>?>(Resource.Loading) // StateFlow to track deletion status
    val deleteAccountStatus: StateFlow<Resource<Unit>?> = _deleteAccountStatus

     fun getCurrentUser() {
        viewModelScope.launch {
            try {
                _currentUser.value = getCurrentUserUseCase()
                Log.v("ProfileViewModel", " success get user data ${_currentUser.value.toString()} :")

            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error in getCurrentUser: ${e.message}")
            }
        }

    }
    fun deleteUserAccount() {
        viewModelScope.launch {
            _deleteAccountStatus.value = Resource.Loading
            try {
                // First, delete the user's posts
                val deletePostsResult = deleteUserPostsUseCase.execute()
                if (deletePostsResult is Resource.Error) {
                    _deleteAccountStatus.value = deletePostsResult
                    return@launch
                }

                // Then delete the user account
                val result = deleteUserAccountUseCase()
                _deleteAccountStatus.value = result
            } catch (e: Exception) {
                _deleteAccountStatus.value = Resource.Error(e)
                Log.e("ProfileViewModel", "Error deleting user account: ${e.message}")
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
            }
        }
    }
}
