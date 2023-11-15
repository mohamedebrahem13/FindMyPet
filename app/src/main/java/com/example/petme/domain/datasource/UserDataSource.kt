package com.example.petme.domain.datasource

interface UserDataSource {
    suspend fun saveUserId(userId: String)
    suspend fun getCurrentUserId(): String?
    suspend fun clearUserData()


}