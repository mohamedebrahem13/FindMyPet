// RemotePostDataSource.kt
package com.example.petme.domain.datasource

import android.net.Uri
import com.example.petme.common.Resource
import com.example.petme.data.model.Post

interface RemotePostDataSource {
    suspend fun uploadImagesAndGetDownloadUrls(imageUris: List<Uri>): Resource<List<String>>
    suspend fun getAllPostsSortedByTimestampRemote(): Resource<List<Post>>
    suspend fun addPostRemote(post: Post): Resource<Unit>
    suspend fun getFirebaseUserUid(): String
    suspend fun getPostsForCurrentUser(): Resource<List<Post>>

}
