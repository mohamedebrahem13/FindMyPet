// RoomPostDataSource.kt
package com.example.petme.data.local.room

import com.example.petme.common.Resource
import com.example.petme.data.local.dao.PostDao
import com.example.petme.data.local.model.PostEntity
import com.example.petme.domain.datasource.LocalPostDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomPostDataSource  @Inject constructor(private val postDao: PostDao) : LocalPostDataSource {

    override suspend fun getAllPostsLocal(): Resource<List<PostEntity>> {
        return try {
            Resource.Success(postDao.getAllPosts())
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun insertAllLocal(posts: List<PostEntity>) {
        postDao.insertAll(posts)
    }

    override suspend fun getPostsForUserLocal(userId: String): Resource<List<PostEntity>> {
        return try {
            Resource.Success(postDao.getPostsForUser(userId))
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun deleteAllPosts() {
        postDao.deleteAllPosts()

    }


}
