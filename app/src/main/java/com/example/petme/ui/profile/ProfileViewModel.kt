package com.example.petme.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petme.common.Resource
import com.example.petme.data.model.User
import com.example.petme.domain.usecase.firebaseUseCase.CheckCurrentUserUseCase
import com.example.petme.domain.usecase.firebaseUseCase.GetCurrentUserUseCase
import com.example.petme.domain.usecase.firebaseUseCase.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val checkCurrentUserUseCase: CheckCurrentUserUseCase
) : ViewModel() {

    private val _currentUser = MutableStateFlow<Resource<User>?>(Resource.Loading)
    val currentUser: StateFlow<Resource<User>?> = _currentUser

    private val _checkCurrentUser = MutableStateFlow<Boolean?>(null)
    val checkCurrentUser: StateFlow<Boolean?> = _checkCurrentUser

    init {
        getCurrentUser()
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            try {
                val result = checkCurrentUserUseCase()
                _checkCurrentUser.value = result
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error in checkCurrentUser: ${e.message}")
                // Handle the error, show a message, or perform other actions as needed
            }
        }
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            try {
                _currentUser.value = getCurrentUserUseCase()
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error in getCurrentUser: ${e.message}")
                // Handle the error, show a message, or perform other actions as needed
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                signOutUseCase()
                Log.v("ProfileViewModel", "user is signOut success :")
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error in signOut: ${e.message}")
                // Handle the error, show a message, or perform other actions as needed
            }
        }
    }
}
