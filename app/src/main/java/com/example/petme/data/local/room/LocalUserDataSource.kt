package com.example.petme.data.local.room

import com.example.petme.data.local.dao.UserDao
import com.example.petme.data.local.model.UserEntity
import com.example.petme.domain.datasource.UserDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class LocalUserDataSource  @Inject constructor(private val userDao: UserDao) : UserDataSource {

    override suspend fun saveUserId(userId: String) {
        withContext(Dispatchers.IO) {
            userDao.insertUser(UserEntity(userId))
        }
    }

    override suspend fun getCurrentUserId(): String? {
        return withContext(Dispatchers.IO) {
            userDao.getUser()?.userId
        }
    }

    override suspend fun clearUserData() {
        withContext(Dispatchers.IO) {
            userDao.deleteUser()
            // Add additional data clearing logic if needed
        }    }
}