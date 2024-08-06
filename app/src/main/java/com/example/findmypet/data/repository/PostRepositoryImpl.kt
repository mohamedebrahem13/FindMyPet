package com.example.findmypet.data.repository

import android.net.Uri
import android.util.Log
import com.example.findmypet.common.Constant
import com.example.findmypet.common.Constant.COLLECTION_PATH
import com.example.findmypet.common.Constant.POSTS
import com.example.findmypet.common.Constant.POST_COUNT
import com.example.findmypet.common.Constant.USERID
import com.example.findmypet.common.Resource
import com.example.findmypet.data.model.Post
import com.example.findmypet.data.model.User
import com.example.findmypet.domain.repository.PostRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import java.net.UnknownHostException
import java.util.UUID
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(private val storage: FirebaseStorage,
                                             private val db: FirebaseFirestore,
                                             private val firebaseAuth: FirebaseAuth
):PostRepository {

    override fun getPostsForUserById(): Flow<Resource<List<Post>>> = flow {
        emit(Resource.Loading)

        val userId = getFirebaseUserUid()

        if (userId.isNotEmpty()) {
            try {
                val postsCollection = db.collection(POSTS)
                val userPostsQuery =
                    postsCollection.whereEqualTo(USERID, userId).get().await()

                val userPosts = userPostsQuery.toObjects(Post::class.java)
                Log.v("userposts", userPosts.toString())

                if (userPosts.isEmpty()) {
                    emit(Resource.Error(Throwable("userPosts is empty")))

                } else {
                    emit(Resource.Success(userPosts)) // Emit successful result

                }

            } catch (e: Throwable) {
                emit(Resource.Error(e))
            }
        } else {
            emit(Resource.Error(Throwable("User not authenticated")))
        }
    }

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

    override fun refreshPosts(lastVisible: DocumentSnapshot?): Flow<Pair<Resource<List<Post>>, DocumentSnapshot?>> =
        flow {
            emit(Resource.Loading to null)
            try {
                var postsQuery = db.collection(POSTS)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(Constant.PAGE_SIZE.toLong())

                if (lastVisible != null) {
                    postsQuery = postsQuery.startAfter(lastVisible)
                }

                val postsSnapshot = postsQuery.get().await()
                val posts = postsSnapshot.toObjects(Post::class.java)
                if (posts.isEmpty()) {
                    emit(Resource.Error(Throwable("NO MORE POSTS")) to null) // No lastVisible for empty page
                } else {
                    if (postsSnapshot.size() > 0) { // Check for last page condition
                        emit(Resource.Success(posts) to postsSnapshot.documents.lastOrNull()) // Return lastVisible
                    }
                }
            } catch (e: Throwable) {
                emit(Resource.Error(e) to null) // No lastVisible on error
            }
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


    override suspend fun addPostRemote(post: Post): Resource<Unit> {
        return try {
            val postsCollection = db.collection(POSTS)
            val newPostDocument = postsCollection.add(post).await()
            // Update the postId in the original post object with the actual value from Firebase
            val postId = newPostDocument.id
            post.postId = postId
            // Update the Firestore document with the postId
            postsCollection.document(postId).set(post).await()
            val userId = post.user?.id
            if (userId != null) {
                val userDocRef = db.collection(COLLECTION_PATH).document(userId)
                userDocRef.update(POST_COUNT, FieldValue.increment(1)).await()
            }

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

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
        return if (userId.isNotEmpty()) {
            try {
                val userRef = db.collection("users").document(userId)

                // Add postIdToAdd to the favoritePosts array in the user document
                userRef.update(Constant.FAVORITE, FieldValue.arrayUnion(postIdToAdd)).await()
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
        return if (userId.isNotEmpty()) {
            try {
                val userRef = db.collection("users").document(userId)

                // Remove postIdToRemove from the favoritePosts array in the user document
                userRef.update(Constant.FAVORITE, FieldValue.arrayRemove(postIdToRemove)).await()
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

    override fun searchPostsByPetName(location: String): Flow<Resource<List<Post>>> = flow {
        emit(Resource.Loading)

        val userId = getFirebaseUserUid()

        if (userId.isNotEmpty()) {
            try {
                emit(Resource.Loading)
                val postsCollection = db.collection(POSTS)
                val userPostsQuery = postsCollection
                    .whereGreaterThanOrEqualTo(
                        "pet_location",
                        location
                    ) // Search by pet_name field (greater than or equal to petName)
                    .whereLessThanOrEqualTo(
                        "pet_location",
                        location + "\uf8ff"
                    ) // Search by pet_name field (less than or equal to petName followed by a Unicode character greater than any other character)
                val userPostsQuerySnapshot = userPostsQuery.get().await()
                val userPosts = userPostsQuerySnapshot.toObjects(Post::class.java)
                Log.v("userposts1", userPosts.toString())
                if (userPosts.isEmpty()) {
                    emit(Resource.Success(userPosts))
                } else {
                    emit(Resource.Success(userPosts))
                }

            } catch (e: Throwable) {
                Log.v("userposts2", "error$e")
                emit(Resource.Error(e))

            }
        } else {
            Log.v("userposts3", "error")

            emit(Resource.Error(Throwable("User not authenticated")))
        }
    }

    override suspend fun getUserPostCount(): Int? {
        val userId = getFirebaseUserUid()

        if (userId.isNotEmpty()) {
            try {
                val userDocRef = db.collection(COLLECTION_PATH).document(userId)
                val userDocSnapshot = userDocRef.get().await()

                if (userDocSnapshot.exists()) {
                    val postCount = userDocSnapshot.getLong(POST_COUNT)
                    return postCount?.toInt()
                }
            } catch (e: Exception) {
                Log.e("PostRepositoryImpl", "Error getting user post count: $e")
            }
        }
        return null // Handle the case where userId is empty or other errors occur
    }

    override suspend fun deleteUserPosts() {
        val userId = getFirebaseUserUid()
        try {
            val postsRef = db.collection(POSTS)
            val querySnapshot = postsRef.whereEqualTo(USERID, userId).get().await()

            if (querySnapshot.isEmpty) {
                // No posts found for the user, consider it a success
                return
            }

            db.runTransaction { transaction ->
                for (document in querySnapshot.documents) {
                    val postRef = postsRef.document(document.id)
                    val post = document.toObject(Post::class.java)

                    transaction.delete(postRef)
                    post?.imageUrls?.forEach { imageUrl ->
                        val imageRef = storage.getReferenceFromUrl(imageUrl)
                        imageRef.delete()
                    }
                }
            }.await()

        } catch (e: Exception) {
            // Log the exception for debugging
            Log.e("FirebasePostDataSource", "Error deleting posts: $e")
            // Handle exception (e.g., report failure)
        }
    }
}