package com.example.findmypet.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findmypet.domain.usecase.firebaseUseCase.auth.UpdateTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(private val updateTokenUseCase: UpdateTokenUseCase) : ViewModel() {

    fun onViewOpened() {
        // Call this method when the view is opened
        updateToken()
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
