package com.example.findmypet.data.repository

import android.net.Uri
import com.example.findmypet.common.Resource
import com.example.findmypet.data.model.Post
import com.example.findmypet.domain.datasource.RemotePostDataSource
import com.example.findmypet.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(private val remotePostDataSource: RemotePostDataSource):PostRepository {


    override fun getPostsForUserById(): Flow<Resource<List<Post>>> {
   return remotePostDataSource.getPostsForUserById()
    }

    override suspend fun uploadImagesAndGetDownloadUrls(imageUris: List<Uri>): Resource<List<String>> {
   return remotePostDataSource.uploadImagesAndGetDownloadUrls(imageUris)   }

    override suspend fun getFirebaseUserUid(): String {
   return remotePostDataSource.getFirebaseUserUid()   }

    override fun refreshPosts(): Flow<Resource<List<Post>>> {
   return remotePostDataSource.refreshPosts()   }

    override fun getFavoritePosts(): Flow<Resource<List<Post>>> {
   return remotePostDataSource.getFavoritePosts()   }

    override suspend fun addPostRemote(post: Post): Resource<Unit> {
   return remotePostDataSource.addPostRemote(post)   }

    override suspend fun deletePostRemote(postId: String): Resource<Unit> {
 return remotePostDataSource.deletePostRemote(postId)   }

    override suspend fun addPostToFavorite(postIdToAdd: String): Resource<Unit> {
 return remotePostDataSource.addPostToFavorite(postIdToAdd)   }

    override suspend fun removePostFromFavorite(postIdToRemove: String): Resource<Unit> {
 return remotePostDataSource.removePostFromFavorite(postIdToRemove)   }

}