package com.example.findmypet.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findmypet.common.Resource
import com.example.findmypet.data.model.User
import com.example.findmypet.domain.usecase.firebaseUseCase.auth.GetCurrentUserUseCase
import com.example.findmypet.domain.usecase.firebaseUseCase.auth.UpdateTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(private val updateTokenUseCase: UpdateTokenUseCase, private val getCurrentUserUseCase: GetCurrentUserUseCase,
) : ViewModel() {
    private val _currentUser = MutableStateFlow<Resource<User>?>(Resource.Loading)
    val currentUser: StateFlow<Resource<User>?> = _currentUser
    fun onViewOpened() {
        // Call this method when the view is opened
        updateToken()
    }
    fun getCurrentUser() {
        viewModelScope.launch {
            try {
                _currentUser.value = getCurrentUserUseCase()
            } catch (e: Exception) {
                Log.e("homeViewModel", "Error in getCurrentUser IN HomeViewModel: ${e.message}")
            }
        }
    }


    private fun updateToken() {
        viewModelScope.launch {
            try {
                updateTokenUseCase.updateTokenFromOtherPartOfApp()
            } catch (e: Exception) {
                Log.v("update token","update token error in the SignInViewModel ${e.message}")
            }
        }
    }



}
