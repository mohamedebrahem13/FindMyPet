package com.example.findmypet.ui.postdetails

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findmypet.domain.usecase.firebaseUseCase.GetCurrentUserUidUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PostDetailsViewModel  @Inject constructor(private val getCurrentUserUidUseCase: GetCurrentUserUidUseCase) : ViewModel(){

    var uid: String = ""


    init {
        setUid()
    }

    private fun setUid() {
        viewModelScope.launch {
            try {
                uid = getCurrentUserUidUseCase()
                Log.v("uid", "user uid$uid ")
            } catch (e: Exception) {
                Log.e(
                    "PostDetailsViewModel",
                    "Error in getCurrentUserUidUseCase in PostDetailsViewModel: ${e.message}"
                )
                // Handle the error, show a message, or perform other actions as needed
            }

        }


    }
}