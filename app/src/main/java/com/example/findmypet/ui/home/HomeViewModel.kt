package com.example.findmypet.ui.home

import androidx.lifecycle.ViewModel
import com.example.findmypet.domain.usecase.firebaseUseCase.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(private val getCurrentUserUseCase: GetCurrentUserUseCase) : ViewModel() {
}
