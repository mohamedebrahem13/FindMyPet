package com.example.findmypet.common

sealed class MessageResource {
    data class Success(val message: String) : MessageResource()
    data class Error(val message: String, val throwable: Throwable) : MessageResource()
}
