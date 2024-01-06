package com.example.findmypet.ui.signin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findmypet.common.Resource
import com.example.findmypet.domain.usecase.firebaseUseCase.CheckCurrentUserUseCase
import com.example.findmypet.domain.usecase.firebaseUseCase.SignInUseCase
import com.example.findmypet.domain.usecase.firebaseUseCase.UpdateTokenUseCase
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(private val signInUseCase: SignInUseCase,private val checkCurrentUserUseCase: CheckCurrentUserUseCase) : ViewModel() {

    private val _result = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val result: StateFlow<Resource<FirebaseUser>?> = _result

    private val _checkCurrentUser = MutableStateFlow<Boolean?>(null)
    val checkCurrentUser: StateFlow<Boolean?> = _checkCurrentUser

    init {
        checkCurrentUser()
    }



    fun signInWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            try {
                _result.emit(Resource.Loading)
                val signInResult = signInUseCase(email, password)
                _result.emit(signInResult)
            } catch (e: Exception) {
                _result.emit(Resource.Error(e))
            }
        }
    }




    private fun checkCurrentUser() {
        viewModelScope.launch {
            try {
                val result = checkCurrentUserUseCase()
                _checkCurrentUser.value = result
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error in checkCurrentUser: ${e.message}")
            }
        }
    }
}