package com.example.petme.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petme.common.Resource
import com.example.petme.data.model.User
import com.example.petme.domain.usecase.firebaseUseCase.CheckCurrentUserUseCase
import com.example.petme.domain.usecase.firebaseUseCase.GetCurrentUserUseCase
import com.example.petme.domain.usecase.firebaseUseCase.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signOutUseCase: SignOutUseCase,private val checkCurrentUserUseCase: CheckCurrentUserUseCase
) : ViewModel() {

    private val _currentUser = MutableLiveData<Resource<User>?>(Resource.Loading)
    val currentUser: MutableLiveData<Resource<User>?> = _currentUser

    private val _checkCurrentUserUseCase =MutableLiveData<Boolean?>()
    val checkCurrentUser: MutableLiveData<Boolean?> =_checkCurrentUserUseCase

    init {
        getCurrentUser()
        checkCurrentUser()
    }


    private fun checkCurrentUser(){
     viewModelScope.launch {
         checkCurrentUser.value= checkCurrentUserUseCase()
}
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            _currentUser.value = getCurrentUserUseCase()
        }
    }

    fun signOut() {
        viewModelScope.launch {
            signOutUseCase()
        }
    }
}