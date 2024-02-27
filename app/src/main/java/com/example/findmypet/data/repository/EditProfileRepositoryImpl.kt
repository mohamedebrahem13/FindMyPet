package com.example.findmypet.data.repository

import android.net.Uri
import com.example.findmypet.common.Constant
import com.example.findmypet.common.Constant.COLLECTION_PATH
import com.example.findmypet.common.Constant.IMAGES
import com.example.findmypet.common.Constant.POSTS
import com.example.findmypet.common.Constant.PROFILE_IMAGE_NAME
import com.example.findmypet.common.Constant.USERID
import com.example.findmypet.common.Resource
import com.example.findmypet.common.UpdateUserProfileResponse
import com.example.findmypet.data.model.User
import com.example.findmypet.domain.repository.AddImageToStorageResponse
import com.example.findmypet.domain.repository.AddImageUrlToFirestoreResponse
import com.example.findmypet.domain.repository.EditProfileRepository
import com.example.findmypet.domain.repository.GetImageUrlFromFirestoreResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EditProfileRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage,
    private val db: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : EditProfileRepository {
    override suspend fun addImageToFirebaseStorage(imageUri: Uri): AddImageToStorageResponse {
        return try {
            val downloadUrl = storage.reference.child(IMAGES).child(getFirebaseUserUid()+PROFILE_IMAGE_NAME)
                .putFile(imageUri).await()
                .storage.downloadUrl.await()
            Resource.Success(downloadUrl)
        } catch (e: Exception) {
            error(e)
        }
    }

    override suspend fun addImageUrlToFirestore(downloadUrl: Uri): AddImageUrlToFirestoreResponse {
        return try {
            db.collection(COLLECTION_PATH).document(getFirebaseUserUid()).update(Constant.PROFILE_IMAGE_PATH,downloadUrl.toString()).await()
            Resource.Success(downloadUrl.toString())
        } catch (e: Exception) {
            error(e)
        }
    }



    override suspend fun getImageUrlFromFirestore(): GetImageUrlFromFirestoreResponse {
        return try {
            val imageUrl = db.collection(COLLECTION_PATH).document(getFirebaseUserUid()).get().await().getString(Constant.PROFILE_IMAGE_PATH)
            Resource.Success(imageUrl.toString())
        } catch (e: Exception) {
            error(e)
        }
    }

    override suspend fun getFirebaseUserUid(): String = firebaseAuth.currentUser?.uid.orEmpty()


    override suspend fun updateUserProfile(user: User): UpdateUserProfileResponse {
        try {
            val userId = getFirebaseUserUid()

            // Update the user's information in Firestore
            val userModel = mapOf(
                Constant.NICKNAME to user.nickname,
                Constant.PHONE_NUMBER to user.phone,
                Constant.E_MAIL to user.email,
                Constant.PROFILE_IMAGE_PATH to user.imagePath ,
                // Add other fields you want to update in the user
            )
            val userRef = db.collection(COLLECTION_PATH).document(userId)
            userRef.update(userModel).await()

            // Fetch posts associated with the updated user from Firestore
            val userPostsQuery = db.collection(POSTS)
                .whereEqualTo(USERID, userId)
                .get()
                .await()

            val userPosts = userPostsQuery.documents

            // Update specific fields in the user object within posts associated with the user
            userPosts.forEach { postSnapshot ->
                val postRef = db.collection(POSTS).document(postSnapshot.id)
                val postUserData = postSnapshot.get("user") as? Map<String, Any>

                // Update specific fields within the user object in the post
                val updatedUserInPost = mutableMapOf<String, Any>().apply {
                    putAll(postUserData ?: mapOf()) // Preserve existing user data if present
                    // Update specific fields in the user object within the post
                    if (user.nickname.isNotEmpty()) put(Constant.NICKNAME, user.nickname)
                    if (user.phone.isNotEmpty()) put(Constant.PHONE_NUMBER, user.phone)
                    if (user.email.isNotEmpty()) put(Constant.E_MAIL, user.email)
                    if (user.imagePath.isNotEmpty()) put(Constant.IMAGE_PASS, user.imagePath)

                    // Add other fields you want to update in the user object
                }

                // Update the user information within the post
                postRef.update("user", updatedUserInPost).await()
            }

            // Return a success response after updating user information in posts
            return UpdateUserProfileResponse.Success
        } catch (e: Exception) {
            // Handle errors and return an error response
            return UpdateUserProfileResponse.Error(e)
        }
    }


}