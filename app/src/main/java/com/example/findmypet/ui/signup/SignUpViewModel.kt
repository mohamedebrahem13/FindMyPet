package com.example.findmypet.ui.signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findmypet.common.Resource
import com.example.findmypet.data.model.User
import com.example.findmypet.domain.usecase.firebaseUseCase.CheckCurrentUserUseCase
import com.example.findmypet.domain.usecase.firebaseUseCase.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private val checkCurrentUserUseCase: CheckCurrentUserUseCase
) : ViewModel() {

    private val _result = MutableLiveData<Resource<Unit>?>()
    val result: MutableLiveData<Resource<Unit>?> = _result

    private val _checkCurrentUser = MutableLiveData<Boolean?>()
    val checkCurrentUser: MutableLiveData<Boolean?> = _checkCurrentUser

    init {
        checkCurrentUser()
    }

    fun signUpWithEmailAndPassword(user: User, password: String) {
        viewModelScope.launch {
            _result.value = Resource.Loading
            _result.value = signUpUseCase(user, password)
        }
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            _checkCurrentUser.value = checkCurrentUserUseCase()
        }
    }
}