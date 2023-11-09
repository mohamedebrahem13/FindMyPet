package com.example.petme.domain.repository

import android.net.Uri
import com.example.petme.common.Resource
import com.example.petme.data.model.Post

interface PostRepository {
    suspend fun addPost(post: Post): Resource<Unit>
    suspend fun uploadImagesAndGetDownloadUrls(imageUris: List<Uri>): Resource<List<String>>
    suspend fun getPostsForCurrentUser(): Resource<List<Post>>
    suspend fun getAllPostsSortedByTimestamp(): Resource<List<Post>>
    suspend fun getFirebaseUserUid(): String

}