package com.example.findmypet.common

sealed class UpdateUserProfileResponse {
    object Loading : UpdateUserProfileResponse()
    object Success : UpdateUserProfileResponse()
    data class Error(val throwable: Throwable) : UpdateUserProfileResponse()
}
