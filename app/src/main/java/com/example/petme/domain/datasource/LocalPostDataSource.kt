// LocalPostDataSource.kt (Domain Layer)
package com.example.petme.domain.datasource

import com.example.petme.common.Resource
import com.example.petme.data.local.model.PostEntity

interface LocalPostDataSource {
    suspend fun getAllPostsLocal(): Resource<List<PostEntity>>
    suspend fun insertAllLocal(posts: List<PostEntity>)
    suspend fun getPostsForUserLocal(userId: String): Resource<List<PostEntity>>
    suspend fun deleteAllPosts()
}