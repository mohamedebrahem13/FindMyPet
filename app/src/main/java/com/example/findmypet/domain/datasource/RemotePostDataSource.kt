package com.example.findmypet.domain.datasource

import android.net.Uri
import com.example.findmypet.common.Resource
import com.example.findmypet.data.model.Post
import kotlinx.coroutines.flow.Flow

interface RemotePostDataSource {
    suspend fun uploadImagesAndGetDownloadUrls(imageUris: List<Uri>): Resource<List<String>>
    suspend fun getFirebaseUserUid(): String
    fun refreshPosts(): Flow<Resource<List<Post>>>
    fun getFavoritePosts(): Flow<Resource<List<Post>>>
    suspend fun deletePostRemote(postId: String): Resource<Unit>
    suspend fun addPostToFavorite(postIdToAdd: String): Resource<Unit>
    suspend fun removePostFromFavorite(postIdToRemove: String): Resource<Unit>
    suspend fun addPostRemote(post: Post): Resource<Unit>
    fun getPostsForUserById(): Flow<Resource<List<Post>>>





}