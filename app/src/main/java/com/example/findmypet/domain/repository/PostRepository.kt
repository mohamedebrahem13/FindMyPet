package com.example.findmypet.domain.repository

import android.net.Uri
import com.example.findmypet.common.Resource
import com.example.findmypet.data.local.model.PostEntity
import com.example.findmypet.data.model.Post

interface PostRepository {
    suspend fun getAllPostsLocal(): Resource<List<PostEntity>>
    suspend fun insertAllLocal(posts: List<PostEntity>)
    suspend fun getPostsForUserById(): Resource<List<Post>>

    suspend fun uploadImagesAndGetDownloadUrls(imageUris: List<Uri>): Resource<List<String>>

    suspend fun getFirebaseUserUid(): String

    suspend fun refreshPosts(): Resource<List<Post>>


    suspend fun addPostRemote(post: Post): Resource<Unit>
    suspend fun clearUserData()
    suspend fun deletePostRemote(postId: String): Resource<Unit>



}