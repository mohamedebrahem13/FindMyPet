package com.example.findmypet.data.remote.firestore

import android.net.Uri
import android.util.Log
import com.example.findmypet.common.Constant.COLLECTION_PATH
import com.example.findmypet.common.Constant.FAVORITE
import com.example.findmypet.common.Constant.POSTS
import com.example.findmypet.common.Constant.USERID
import com.example.findmypet.common.Resource
import com.example.findmypet.data.model.Post
import com.example.findmypet.data.model.User
import com.example.findmypet.domain.datasource.RemotePostDataSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import java.net.UnknownHostException
import java.util.*
import javax.inject.Inject

class FirebasePostDataSource @Inject constructor(
    private val storage: FirebaseStorage,
    private val db: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : RemotePostDataSource {

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

    override suspend fun getFirebaseUserUid(): String = firebaseAuth.currentUser?.uid.orEmpty()




    override  fun refreshPosts(): Flow<Resource<List<Post>>> = flow {
        try {

            emit(Resource.Loading) // Emit loading state

            val postsCollection = db.collection(POSTS)
            val allPostsQuery = postsCollection
                .orderBy("timestamp", Query.Direction.DESCENDING) // Sort by timestamp in descending order
                .get()
                .await()

            val allPosts = allPostsQuery.toObjects(Post::class.java)
            if(allPosts.isNotEmpty()){
                emit(Resource.Success(allPosts)) // Emit successful result
            }else{
                emit(Resource.Error(Throwable("list is empty")))
            }

        } catch (e: Throwable) {
            emit(Resource.Error(e)) // Emit error if there's an exception
        }
    }.catch { e ->
        emit(Resource.Error(e)) // Catch any exceptions in the flow
    }

    override fun getFavoritePosts(): Flow<Resource<List<Post>>> = flow {
        val userId = getFirebaseUserUid()

        if (userId.isNotEmpty()) {
            try {
                val userRef = db.collection(COLLECTION_PATH).document(userId).get().await()
                val user = userRef.toObject(User::class.java)

                val favoritePostIds = user?.favoritePosts ?: emptyList()

                val postsList = mutableListOf<Post>()
                for (postId in favoritePostIds) {
                    try {
                        val postRef = db.collection(POSTS).document(postId).get().await()
                        val post = postRef.toObject(Post::class.java)
                        post?.let { postsList.add(it) }
                    } catch (fetchException: Exception) {
                        // Handle specific exceptions here
                        if (fetchException is UnknownHostException) {
                            // Handle network-related issues here
                            emit(Resource.Error(fetchException))
                            return@flow
                        }
                        if (fetchException is FirebaseFirestoreException) {
                            // Handle Firestore-related exceptions
                            emit(Resource.Error(fetchException))
                            return@flow
                        }
                        // Handle other exceptions if needed
                    }
                }

                emit(Resource.Success(postsList))
            } catch (e: FirebaseFirestoreException) {
                // Handle Firestore-related exceptions outside the loop
                emit(Resource.Error(e))
            } catch (e: Exception) {
                // Handle other exceptions outside the loop
                emit(Resource.Error(e))
            }
        } else {
            emit(Resource.Error(Exception("User not authenticated")))
        }
    }.flowOn(Dispatchers.IO)


    override suspend fun deletePostRemote(postId: String): Resource<Unit> {
        return try {

            val postRef = db.collection(POSTS).document(postId)
            val postSnapshot = postRef.get().await()

            if (postSnapshot.exists()) {
                val post = postSnapshot.toObject(Post::class.java)

                db.runTransaction { transaction ->
                    transaction.delete(postRef)
                    post?.imageUrls?.forEach { imageUrl ->
                        val imageRef = storage.getReferenceFromUrl(imageUrl)
                        imageRef.delete()
                    }
                }.await()

                Resource.Success(Unit)
            } else {
                // Post doesn't exist, consider it a success
                Resource.Success(Unit)
            }
        } catch (e: Exception) {
            // Log the exception for debugging
            Log.e("FirebasePostDataSource", "Error deleting post: $e")
            // Return a more specific error message or different Resource.Error state if needed
            Resource.Error(e)
        }
    }

    override suspend fun addPostToFavorite(postIdToAdd: String): Resource<Unit> {
        val userId = getFirebaseUserUid()
        return if(userId.isNotEmpty()) {
            try {
                val userRef = db.collection("users").document(userId)

                // Add postIdToAdd to the favoritePosts array in the user document
                userRef.update(FAVORITE, FieldValue.arrayUnion(postIdToAdd)).await()
                // Post ID added to favorites successfully

                Resource.Success(Unit)
            } catch (e: Exception) {
                Resource.Error(e)
            }
        } else {
            // Handle the case where there is no authenticated user
            Resource.Error(Exception("User not authenticated"))
        }
    }


    override suspend fun removePostFromFavorite(postIdToRemove: String): Resource<Unit> {
        val userId = getFirebaseUserUid()
        return if(userId.isNotEmpty()) {
            try {
                val userRef = db.collection("users").document(userId)

                // Remove postIdToRemove from the favoritePosts array in the user document
                userRef.update(FAVORITE, FieldValue.arrayRemove(postIdToRemove)).await()
                // Post ID removed from favorites successfully

                Resource.Success(Unit)
            } catch (e: Exception) {
                Resource.Error(e)
            }
        } else {
            // Handle the case where there is no authenticated user
            Resource.Error(Exception("User not authenticated"))
        }
    }



    override suspend fun addPostRemote(post: Post): Resource<Unit> {
        return try {
            val postsCollection = db.collection(POSTS)
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



    override fun getPostsForUserById(): Flow<Resource<List<Post>>> = flow {
        emit(Resource.Loading)

        val userId = getFirebaseUserUid()

        if (userId.isNotEmpty()) {
            try {
                val postsCollection = db.collection(POSTS)
                val userPostsQuery = postsCollection.whereEqualTo(USERID, userId).get().await()

                val userPosts = userPostsQuery.toObjects(Post::class.java)
                Log.v("userposts", userPosts.toString())

                if(userPosts.isEmpty()){
                    emit(Resource.Error(Throwable("userPosts is empty")))

                }else{
                    emit(Resource.Success(userPosts)) // Emit successful result

                }

            } catch (e: Throwable) {
                emit(Resource.Error(e))
            }
        } else {
            emit(Resource.Error(Throwable("User not authenticated")))
        }
    }
}