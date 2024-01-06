package com.example.findmypet.domain.repository

import com.example.findmypet.data.model.User
import com.google.firebase.auth.AuthResult

interface Authenticator {
    suspend fun getFirebaseUserUid(): String

    suspend fun signUpWithEmailAndPassword(user: User, password: String)

    suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult

    suspend fun sendPasswordResetEmail(email: String): Void

    suspend fun isCurrentUserExist(): Boolean

    suspend fun getCurrentUser(): User

    suspend fun signOut()
    suspend fun firebaseMessagingToken(): String
    suspend fun updateTokenForCurrentUser(newToken: String)}
