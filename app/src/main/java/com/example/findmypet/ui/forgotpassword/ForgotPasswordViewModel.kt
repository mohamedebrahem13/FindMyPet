package com.example.findmypet.ui.forgotpassword

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findmypet.common.Resource
import com.example.findmypet.domain.usecase.firebaseUseCase.ForgotPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(private val forgotPasswordUseCase: ForgotPasswordUseCase) :
    ViewModel() {

    private val _result = MutableLiveData<Resource<Void>?>()
    val result: MutableLiveData<Resource<Void>?> = _result

    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            _result.value = Resource.Loading
            _result.value = forgotPasswordUseCase(email)
        }
    }
}