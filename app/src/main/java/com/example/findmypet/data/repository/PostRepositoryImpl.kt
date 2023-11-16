package com.example.findmypet.data.repository

import android.net.Uri
import com.example.findmypet.common.Resource
import com.example.findmypet.data.local.model.PostEntity
import com.example.findmypet.data.local.room.LocalUserDataSource
import com.example.findmypet.data.model.Post
import com.example.findmypet.data.model.User
import com.example.findmypet.domain.datasource.LocalPostDataSource
import com.example.findmypet.domain.datasource.RemotePostDataSource
import com.example.findmypet.domain.repository.PostRepository
import java.util.*
import javax.inject.Inject

class PostRepositoryImpl  @Inject constructor(
    private val localPostDataSource: LocalPostDataSource,
    private val remotePostDataSource: RemotePostDataSource,
    private val localUserDataSource: LocalUserDataSource
) : PostRepository {

    override suspend fun getAllPostsLocal(): Resource<List<PostEntity>> {
        return localPostDataSource.getAllPostsLocal()
    }

    override suspend fun insertAllLocal(posts: List<PostEntity>) {
        localPostDataSource.insertAllLocal(posts)
    }

    override suspend fun getPostsForUserById(): Resource<List<Post>> {
        return try {
            // Attempt to get posts from the network
            val remotePostsResult = remotePostDataSource.getPostsForCurrentUser()

            if (remotePostsResult is Resource.Success) {
                // If successful, return the posts with image URLs
                Resource.Success(remotePostsResult.data)
            } else {
                // If fetching from the network fails, attempt to get posts from the local database
                val userId = getFirebaseUserUid()
                val localPostsResult = localPostDataSource.getPostsForUserLocal(userId)

                // If the local data is not empty, return it
                if (localPostsResult is Resource.Success && localPostsResult.data.isNotEmpty()) {
                    val posts = localPostsResult.data.map { it.toPost() }
                    Resource.Success(posts)
                } else {
                    // If both network and local fetching fail, return an error
                    Resource.Error(Exception("Failed to get posts for user"))
                }
            }
        } catch (e: Exception) {
            // Handle exceptions
            Resource.Error(e)
        }
    }


    override suspend fun uploadImagesAndGetDownloadUrls(imageUris: List<Uri>): Resource<List<String>> {
        return remotePostDataSource.uploadImagesAndGetDownloadUrls(imageUris)
    }

    override suspend fun getFirebaseUserUid(): String {
        return localUserDataSource.getCurrentUserId() ?: remotePostDataSource.getFirebaseUserUid()
            .also { localUserDataSource.saveUserId(it) }
    }

    override suspend fun refreshPosts(): Resource<List<Post>> {
        try {
            // Attempt to get posts from the network
            val remotePostsResult = remotePostDataSource.getAllPostsSortedByTimestampRemote()

            if (remotePostsResult is Resource.Success) {
                // If successful, clear the local database and update it with the fresh data
                localPostDataSource.deleteAllPosts()
                val postEntities = remotePostsResult.data.map { it.toPostEntity() }
                localPostDataSource.insertAllLocal(postEntities)

                return remotePostsResult
            }

            // If fetching from the network fails, attempt to get posts from the local database
            val localPostsResult = localPostDataSource.getAllPostsLocal()

            // If the local data is not empty and fresh, return it
            if (localPostsResult is Resource.Success && localPostsResult.data.isNotEmpty()) {
                val posts = localPostsResult.data.map { it.toPost() }
                return Resource.Success(posts)
            }

            // If the local data is not fresh or empty, return an error
            return Resource.Error(Exception("Local database is empty or not fresh"))

        } catch (e: Exception) {
            return Resource.Error(e)
        }
    }



    // Extension functions to convert between PostEntity and Post
    private fun PostEntity.toPost(): Post {
        return Post(
            postId = postId,
            pet_name = pet_name,
            pet_description = pet_description,
            pet_age = pet_age,
            pet_gender = pet_gender,
            pet_location = pet_location,
            user = User(userId),
            timestamp = timestamp
        )
    }

    private fun Post.toPostEntity(): PostEntity {
        return PostEntity(
            postId = postId!!,
            pet_name = pet_name,
            pet_description = pet_description,
            pet_age = pet_age,
            pet_gender = pet_gender,
            imageUrls = imageUrls, // Include actual imageUrls from your Post model
            pet_location = pet_location,
            userId = user?.userId.orEmpty(),
            timestamp = timestamp ?: Date(),
        )
    }

    override suspend fun addPostRemote(post: Post): Resource<Unit> {
        return remotePostDataSource.addPostRemote(post)
    }

    override suspend fun clearUserData() {
   localUserDataSource.clearUserData()
    }

    override suspend fun deletePostRemote(postId: String): Resource<Unit> {
        return remotePostDataSource.deletePostRemote(postId)
    }


}