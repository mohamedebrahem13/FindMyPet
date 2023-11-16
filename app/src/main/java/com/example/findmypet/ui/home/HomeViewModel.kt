package com.example.findmypet.ui.home

import androidx.lifecycle.ViewModel
import com.example.findmypet.domain.usecase.firebaseUseCase.GetCurrentUserUseCase
import com.example.findmypet.domain.usecase.firebaseUseCase.posts.GetPostsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(private val getCurrentUserUseCase: GetCurrentUserUseCase,private val getPostsUseCase: GetPostsUseCase) : ViewModel() {
}
