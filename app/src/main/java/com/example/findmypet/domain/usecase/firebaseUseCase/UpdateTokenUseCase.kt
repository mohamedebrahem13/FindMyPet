package com.example.findmypet.domain.usecase.firebaseUseCase

import android.util.Log
import com.example.findmypet.domain.repository.Authenticator

class UpdateTokenUseCase(private val authenticator: Authenticator) {

    suspend fun updateTokenFromOtherPartOfApp() {
        try {
            val newToken = authenticator.firebaseMessagingToken()
            authenticator.updateTokenForCurrentUser(newToken)
        } catch (e: Exception) {
            Log.v("update token","update token error in the use case  ${e.message}")
        }
    }
}