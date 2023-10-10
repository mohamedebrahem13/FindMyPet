package com.example.petme.ui.signin

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petme.common.Resource
import com.example.petme.domain.usecase.firebaseUseCase.CheckCurrentUserUseCase
import com.example.petme.domain.usecase.firebaseUseCase.SignInUseCase
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

    private val _checkCurrentUser = MutableLiveData<Boolean?>()
    val checkCurrentUser: MutableLiveData<Boolean?> = _checkCurrentUser

    init {
        checkCurrentUser()
    }



    fun signInWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            _result.emit(Resource.Loading)
            _result.emit (signInUseCase(email, password))
        }
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            _checkCurrentUser.value = checkCurrentUserUseCase()
        }
    }
}