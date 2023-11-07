package com.example.petme.data.repository

import android.net.Uri
import com.example.petme.common.Constant
import com.example.petme.common.Constant.USERID
import com.example.petme.common.Resource
import com.example.petme.data.model.Post
import com.example.petme.domain.repository.PostRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

class FirebasePostRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) : PostRepository {

    override suspend fun uploadImagesAndGetDownloadUrls(imageUris: List<Uri>): Resource<List<String>> {
        return try {
            val downloadUrls = mutableListOf<String>()
            val storageRef = storage.reference

            for (imageUri in imageUris) {
                val imageFileName = UUID.randomUUID().toString()
                val imageRef = storageRef.child("images/$imageFileName")

                // Upload the image and wait for the result
                imageRef.putFile(imageUri).await()

                // Get the download URL after a successful upload
                val downloadUrl = imageRef.downloadUrl.await().toString()
                downloadUrls.add(downloadUrl)
            }

            Resource.Success(downloadUrls)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun getPostsForUser(userId: String): Resource<List<Post>> {
        return try {
            val postsCollection = db.collection(Constant.POSTS)
            val userPostsQuery = postsCollection.whereEqualTo(USERID, userId).get().await()

            val userPosts = userPostsQuery.toObjects(Post::class.java)
            Resource.Success(userPosts)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun getAllPostsSortedByTimestamp(): Resource<List<Post>> {
        return try {
            val postsCollection = db.collection(Constant.POSTS)
            val allPostsQuery = postsCollection
                .orderBy("timestamp", Query.Direction.DESCENDING) // Sort by timestamp in descending order
                .get()
                .await()

            val allPosts = allPostsQuery.toObjects(Post::class.java)
            Resource.Success(allPosts)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }


    override suspend fun addPost(post: Post): Resource<Unit> {
        return try {
            val postsCollection = db.collection(Constant.POSTS)
            val newPostDocument = postsCollection.add(post).await()
            // Update the postId in the original post object with the actual value from Firebase
            val postId = newPostDocument.id
            post.postId = postId
            // Update the Firestore document with the postId
            postsCollection.document(postId).set(post).await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}